read_xyz_file(File, Points) :-
 open(File, read, Stream),
read_xyz_points(Stream,Points),
 close(Stream).

read_xyz_points(Stream, []) :-
 at_end_of_stream(Stream).

read_xyz_points(Stream, [Point|Points]) :-
 \+ at_end_of_stream(Stream),
read_line_to_string(Stream,L), split_string(L, "\t", "\s\t\n", 
XYZ), convert_to_float(XYZ,Point),
 read_xyz_points(Stream, Points).
convert_to_float([],[]).

convert_to_float([H|T],[HH|TT]) :-
 atom_number(H, HH),
 convert_to_float(T,TT). 


% generate three random points from a larger list of Point
% Points - The larger list of Points
% Point3 - The list of 3 random points  

random3points(Points, Point3) :-
    length(Points, N),
    N >= 3, %check if there are at least 3 elements in the list
    random_permutation(Points, Shuffled),
    append(Point3,_,Shuffled),
    length(Point3,3).

% random3points([[1,2,3],[4,5,6],[7,8,9],[10,11,12],[13,14,15]],Point3_1).


% get the plane from 3 points 
% Point3 - list of 3 Points
% Plane - the plane from 3 points, in form of [a,b,c,d]
%       Methodology
%        |   i      j      k   |
%        | x2-x1  y2-y1  z2-z1 | = (a,b,c)
%        | x3-x1  y3-y1  z3-z1 |
%        a = (y2-y1)*(z3-z1) - (y3-y1)*(z2-z1)
%        b = (z2-z1)*(x3-x1) - (x2-x1)*(z3-z1)
%        c = (x2-x1)*(y3-y1) - (y2-y1)*(x3-x1)
%        d = -(ax1 + by1 + cz1)

plane(Point3, Plane):-
    Point3 = [[X1,Y1,Z1],[X2,Y2,Z2],[X3,Y3,Z3]],
    A is (Y2-Y1)*(Z3-Z1) - (Y3-Y1)*(Z2-Z1),
    B is (Z2-Z1)*(X3-X1) - (X2-X1)*(Z3-Z1),
    C is (X2-X1)*(Y3-Y1) - (Y2-Y1)*(X3-X1),
    D is -(A * X1 + B * Y1 + C * Z1),
    Plane = [A, B, C, D].

%test plane
% plane([[1,0,0],[0,1,0],[0,0,1]], Plane1). %expected : [1,1,1,-1]
% plane([[1,1,1],[1,2,0],[-1,2,1]], Plane2). %expected : [1,2,2,-5]

% auxiliary method
% get the distance of a point to the Plane.
% Point - the point
% Plane - the plane
% Distance - the distance of the point to the plane

getDistance(Point, Plane, Distance) :-
     [A, B, C, D] = Plane,
     [X,Y,Z] = Point,
     Numerator is abs(A * X + B * Y + C * Z + D),
     Denominator is sqrt(A * A + B * B + C * C),
     Distance is Numerator/Denominator.

%test case
% getDistance([1,2,5], [1,2,5,3], TheDistance). %expected : 6.0249...


% get the support of a PointsList, support is set to 0 initially.
% Plane - the plane be tested
% Points - the PointsList
% Eps - the epsilon value
% N - the support of the plane 
support(Plane, Points, Eps, N) :-
    support_second(Plane, Points, Eps, 0, N).

% auxiliary recursive method for support
% Plane,Points,Eps - same as support method
% N0 - used in next recursive to obtain the current support (work as temp)
% N - the final support of the plane(after all the points are tested)

support_second(_, [], _, N,N):-
    !. %base case when the list is empty

support_second(Plane, [Point|Points], Eps,N0, N) :-
    getDistance(Point, Plane, Distance),
    (Distance < Eps ->
        NN is N0 + 1;
        NN = N0),
    support_second(Plane, Points, Eps, NN,N).

%test case
% support([1, 2, 3, 4],[[1, 1, 1], [2, 2, 2], [3, 3, 3], [4, 4, 4]], 5, Best).
%expected : X = 2

% support([1, 2, 3, 4],[[1, 1, 1], [2, 2, 2], [3, 3, 3], [4, 4, 4]], 98, 4).
%expected: true


% the number of times that the program run based on the Confidence and Percentage.
% Confidence - Confidence
% Percentage - Percentage of point in the Plane
% N - number of times.

ransac-number-of-iterations(Confidence, Percentage, N) :-
    Numerator is log(1 - Confidence),
    Denominator is log(1 - Percentage**3),
    N is round(Numerator / Denominator).

%test case
% ransac-number-of-iterations(0.95, 0.8, K1). %expected : 4 (4.17....)
% ransac-number-of-iterations(0.95, 0.27, K2). %expected:151 (150.69...)


%test plane
test(plane, 1) :- plane([[1,0,0],[0,1,0],[0,0,1]], [1,1,1,-1]),!.
test(plane, 2) :- plane([[1,1,1],[1,2,0],[-1,2,1]], [1,2,2,-5]),!.

%test support
test(support, 1):-
    support([1, 2, 3, 4],[[1, 1, 1], [2, 2, 2], [3, 3, 3], [4, 4, 4]], 5, 2), !.

test(support, 2):-
    support([1, 2, 3, 4],[[1, 1, 1], [2, 2, 2], [3, 3, 3], [4, 4, 4]], 98, 4), !.

%test case
test(ransac-number-of-iterations, 1):-
 ransac-number-of-iterations(0.95, 0.8, 4), !.
test(ransac-number-of-iterations, 2):-
 ransac-number-of-iterations(0.95, 0.27, 151), !. 

%test 
test(Random3Points, Times, PointsList) :-
    findall(Point3, (between(1, Times, _),
    random3points([[1,2,3],[4,5,6],[7,8,9],[10,11,12],[13,14,15]],
     Point3)),PointsList).