(in-package :asdf)

(defsystem dic
  :name "dic"
  :version "0.0.1"
  :author "Takeru Ohta"
  
  :serial t
  :components ((:file "package")
               (:file "util")
               (:file "pairing-heap")
               (:file "trie/code-stream")
               (:file "trie/trie")
               (:file "trie/node-allocator")
               (:file "trie/double-array")
               (:file "dic")))
