(ns intcode.vm-state)

(def halt-opcode 99)

(def position-mode 0)
(def immediate-mode 1)
(def relative-mode 2)

(defmethod print-method clojure.lang.PersistentQueue [this ^java.io.Writer w]
  (.write w (str (vec this))))

(defn create-intcode-vm [program & {:keys [inputs memory-size]
                                    :or {inputs [] memory-size 0}}]
  {:memory (into [] (concat program (replicate (- memory-size (count program)) 0)))
   :pc 0
   :relative-base 0
   :inputs (into clojure.lang.PersistentQueue/EMPTY inputs)
   :outputs []})

;; Memory

(defn read-int-at [vm-state address]
  {:pre [(< address (count (:memory vm-state)))]}
  (get-in vm-state [:memory address]))

(defn write-int-at [vm-state address value]
  {:pre [(<= address (count (:memory vm-state)))]}
  (assoc-in vm-state [:memory address] value))

;; Program Counter

(defn increment-pc [vm-state n]
  (update-in vm-state [:pc] + n))

(defn set-pc [vm-state n]
  (assoc vm-state :pc n))

;; Opcode and parameter modes

(defn read-first-instruction-value [vm-state]
  (read-int-at vm-state (:pc vm-state)))

(defn- split-to-reverse-digits [n]
  (->> n
       (iterate #(quot % 10))
       (take-while pos?)
       (mapv #(mod % 10))))

(defn parse-first-instruction-value [first-value]
  (let [reverse-digits (split-to-reverse-digits first-value)]
    {:opcode (+ (nth reverse-digits 0)
                (* 10 (nth reverse-digits 1 0)))
     :parameter-modes [(nth reverse-digits 2 0)
                       (nth reverse-digits 3 0)
                       (nth reverse-digits 4 0)]}))

(defn- read-opcode [vm-state]
  (-> vm-state
      read-first-instruction-value
      parse-first-instruction-value
      :opcode))

(defn halted? [vm-state]
  (= halt-opcode
     (read-opcode vm-state)))

;; Parameters

(defn parameter-address [{pc :pc :as vm-state} n mode]
  {:pre [(<= n 3)]}
  (let [pointer-to-param (+ n pc)]
    (condp = mode
      position-mode (read-int-at vm-state pointer-to-param)
      immediate-mode pointer-to-param
      relative-mode (+ (read-int-at vm-state pointer-to-param)
                       (:relative-base vm-state))
      :else (throw (AssertionError. (str "Unsupported mode: " mode))))))

(defn parameter-value [vm-state n mode]
  (read-int-at vm-state (parameter-address vm-state n mode)))

;; Inputs and outputs

(defn read-input [vm-state]
  {:pre [(not (empty? (:inputs vm-state)))]}
  (peek (:inputs vm-state)))

(defn add-input [{inputs :inputs :as vm-state} input-value]
  (assoc vm-state :inputs (conj inputs input-value)))

(defn add-inputs [{inputs :inputs :as vm-state} input-values]
  (if (empty? input-values)
    vm-state
    (recur (add-input vm-state (first input-values)) (rest input-values))))

(defn drop-input [{inputs :inputs :as vm-state}]
  (assoc vm-state :inputs (pop inputs)))

(defn needs-input? [vm-state]
  (and (empty? (:inputs vm-state))
       (= 3 (read-opcode vm-state))))

(defn add-output [{outputs :outputs :as vm-state} output-value]
  (assoc vm-state :outputs (conj outputs output-value)))

(defn drop-outputs [vm-state]
  (assoc vm-state :outputs []))

;; Relative base

(defn adjust-relative-base [vm-state amount]
  (assoc vm-state
         :relative-base
         (+ (:relative-base vm-state) amount)))
