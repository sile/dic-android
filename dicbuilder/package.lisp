(defpackage dic.util
  (:use :common-lisp)
  (:export write-int
           read-int
           package-alias
           nlet
           a.if
           unique
           with-time
           with-open-binary-output-file
           copy-file
           it))

(defpackage dic
  (:use :common-lisp :dic.util)
  (:export build
           *text-dictionary-charset*
           
           *path.surface-id*
           *path.code-map*
           *path.id2offsets*
           *path.offsets*
           *path.data*
           
           done))
(in-package :dic)

(defvar *text-dictionary-charset* :utf-8)
(defvar *end-of-entry* "-=+=-=+=-=+=-=+=-=+=-")
(defvar *fastest* '(optimize (speed 3) (safety 0) (debug 0)))

(defvar *path.surface-id* #P"surface-id.bin")
(defvar *path.code-map* #P"code-map.bin")
(defvar *path.id2offsets* #P"id2offsets.bin")
(defvar *path.offsets* #P"offsets.bin")
(defvar *path.data* #P"dic.dat")
