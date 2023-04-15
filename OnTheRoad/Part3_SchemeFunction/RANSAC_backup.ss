#lang scheme

;global variable epsilon, For test only(perhps)
(define eps 0.2)

(define (readXYZ fileIn) 
 (let ((sL (map (lambda s (string-split (car s))) 
 (cdr (file->lines fileIn)))))
 (map (lambda (L)
 (map (lambda (s)
 (if (eqv? (string->number s) #f)
 s
(string->number s))) L)) sL)))

;Test for readXYZ
; (readXYZ "Point_Cloud_1_No_Road_Reduced.xyz") ; Return result in form of 2d list


(define (pick3rps Ps)
    (let* ((n (length Ps)) ; assume there is always more than 3 points
        (index1 (random n)) ;take three points' index
        (index2 (random n))
        (index3 (random n)))
    (if (different index1 index2 index3) ;check if three index are equal
        (list (list-ref Ps index1)
        (list-ref Ps index2)
        (list-ref Ps index3))
    (pick3rps Ps)))) ; else clause, do it again

;auxiliary function for check equal
(define (different one two three)
    (not (or (= one two)
        (= one three)
        (= two three))))

;Test for pick3rps

;(define tempList '(10 4568 84 35 95 15 46))
;(define tempList2 '((10 4568 84)(9 8 4)(64 87 12)(46 78 22)))
;(pick3rps tempList)
;(pick3rps tempList2)

; Methodology:
; p1（x1,y1,z1），p2(x2,y2,z2)，p3(x3,y3,z3)
; p1p2（x2-x1,y2-y1,z2-z1), p1p3(x3-x1,y3-y1,z3-z1)
; n = p1p2 x p1p3 cross product
;
    ; |   i      j      k   |
    ; | x2-x1  y2-y1  z2-z1 | = (a,b,c)
    ; | x3-x1  y3-y1  z3-z1 |
;
; a = (y2-y1)*(z3-z1) - (y3-y1)*(z2-z1)
; b = (z2-z1)*(x3-x1) - (x2-x1)*(z3-z1)
; c = (x2-x1)*(y3-y1) - (y2-y1)*(x3-x1)
; d = -(ax1 + by1 + cz1)

(define (plane P1 P2 P3)
    (let* ((a (- (* (- (cadr P2) (cadr P1)) (- (caddr P3) (caddr P1))) (* (- (cadr P3) (cadr P1)) (- (caddr P2) (caddr P1)))))
        (b (- (* (- (caddr P2) (caddr P1)) (- (car P3) (car P1))) (* (- (car P2) (car P1)) (- (caddr P3) (caddr P1)))))
        (c (- (* (- (car P2) (car P1)) (- (cadr P3) (cadr P1))) (* (- (cadr P2) (cadr P1)) (- (car P3) (car P1)))))
        (d (- (+ (* a (car P1)) (* b (cadr P1))(* c (caddr P1))))))
    (list a b c d)))

;Test for plane
;(plane '(1 1 1) '(1 2 0) '(-1 2 1)) ;expected : '(1.0 2.0 2.0 -5.0)

(define (getDistance plane pt)
    (let* ((numer (abs (+ (* (car plane) (car pt)) (* (cadr plane) (cadr pt)) (* (caddr plane) (caddr pt)) (cadddr plane))))
        (denomin (sqrt (+ (sqr (car plane)) (sqr (cadr plane)) (sqr (caddr plane))))))
    (/ numer denomin)))

(define (support plane points)
    (let* ((n (length points))
           (temp points))
        (do ((i 0 (+ i 1))
             (temp points (cdr temp))
            (sup 0 (if (> eps (getDistance plane (car temp))) (+ sup 1)(+ sup 0))))
        ((= i n) (cons sup (cons plane '()))))))


;Test for both functions
;(support '(1 2 2 -5) '((0 0 0) (2 5 7) (1 2 1) (1 1 1)))
;(getDistance '(1 2 2 -5) '(1 2 1) )


;auxiliary function for combine the above functions
(define (findDominOneTime Ps)
    (let* ((pointList (pick3rps Ps))
        (thePlane (plane (car pointList) (cadr pointList) (caddr pointList))))
    (support thePlane Ps)))

;Find the dominant plane by running the above functions k times
(define (dominantPlane Ps k)
  (let loop ((i 1)
             (best 0)
             (bestPlane '(0 0 0 0)))
    (if (= i k)
        (cons best (cons bestPlane '()))
        (let* ((sup-plane (findDominOneTime Ps))
               (sup (car sup-plane))
               (plane (cadr sup-plane)))
          (let ((bestSupport (if (> sup best)
                                  (cons sup (cons plane '()))
                                  (cons best (cons bestPlane '())))))
            (loop (+ i 1) (car bestSupport) (cadr bestSupport)))))))

;(dominantPlane '((0 0 0) (2 5 7) (1 2 1) (1 1 1) (6 7 8) (0.4 0.5 0.6) (0.15 0.36 0.98)) 6)

;k= log( 1 - C ) / log( 1- p^3 )

(define (ransacNumberOfIteration confidence percentage)
    (let ((numerator (log (- 1 confidence)))
        (denominator (log (- 1 (expt percentage 3)))))
    (round (/ numerator denominator))))

;(ransacNumberOfIteration 0.8 0.25)

(define (planeRANSAC filename confidence percentage eps)
    (let* ((Ps (readXYZ filename))
            (k (ransacNumberOfIteration confidence percentage))
            (eps eps))
    (dominantPlane Ps k)))

(planeRANSAC "Point_Cloud_1_No_Road_Reduced.xyz" 0.8 0.3 0.2)
(planeRANSAC "Point_Cloud_2_No_Road_Reduced.xyz" 0.8 0.3 0.2)
(planeRANSAC "Point_Cloud_3_No_Road_Reduced.xyz" 0.8 0.3 0.2)

; (cons 5 (cons '(1 2 3 4) '()))
; (cons 5 '(1 2 3 4))
; (pair? (cons 5 (cons '(1 2 3 4) '())))
; (pair? (cons 5 '(1 2 3 4)))