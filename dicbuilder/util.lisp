(in-package :dic.util)

(declaim (inline write-int read-int))

(defun write-int (int stream &key (width 1))
  (declare ((member 1 2 4 8) width))
  (flet ((write-impl (pos) 
           (write-byte (ldb (byte 8 (* pos 8)) int) stream)))
    (declare (inline write-impl))
    (loop FOR i FROM (1- width) DOWNTO 0 DO (write-impl i))))

(defun read-int (in &key (width 1))
  (declare ((member 1 2 4 8) width))
  (loop FOR i FROM (1- width) DOWNTO 0
        SUM (ash (read-byte in) (* i 8))))

(defmacro package-alias (package &rest alias-list)
  `(eval-when (:compile-toplevel :load-toplevel :execute)
     (rename-package ,package ,package ',alias-list)))

(defmacro nlet (fn-name letargs &body body)
  `(labels ((,fn-name ,(mapcar #'car letargs)
              ,@body))
     (,fn-name ,@(mapcar #'cadr letargs))))

(defmacro a.if (exp then else)
  `(let ((it ,exp))
     (if it
         ,then
       ,else)))

(defun unique (keys)
  (loop FOR (prev cur) ON keys
        WHILE cur
        UNLESS (string= (the simple-string prev) (the simple-string cur))
    COLLECT cur INTO list
    FINALLY
    (return (cons (first keys) list))))

(defmacro with-time (msg &body body)
  `(let ((#1=#:beg (get-internal-real-time)))
     (format *error-output* ";  ~A ... " ,msg)
     (force-output *error-output*)
     (prog1 (progn ,@body)
       (format *error-output* "~,3f sec~%" (/ (- (get-internal-real-time) #1#)
                                            INTERNAL-TIME-UNITS-PER-SECOND))
       (force-output *error-output*))))

(defmacro with-open-binary-output-file ((stream path) &body body)
  `(with-open-file (,stream ,path
                            :direction :output 
                            :if-exists :supersede
                            :element-type '(unsigned-byte 8))
     ,@body))

(defun copy-file (from to)
  (with-open-file (in from :element-type '(unsigned-byte 8))
    (with-open-file (out to :element-type '(unsigned-byte 8)
                            :direction :output
                            :if-exists :supersede)
      (loop FOR octet = (read-byte in nil nil)
            WHILE octet
            DO (write-byte octet out)))))
