(ns data.core
  (:use arcadia.core)
  (:require [clojure.edn :as edn]
            [arcadia.hydrate :as hydrate])
  (:import [UnityEngine Vector3]))

(defn- vector* [^double x ^double y ^double z]
  (Vector3. x y z))

; (def star-names (future (->> "Assets/Hack/data/star-names.edn"
; slurp
; edn/read-string
; (reduce (fn [acc star] 
; (assoc acc
; (apply vector* ((juxt :x :y :z) star))
; (star :name)))
; {} ))))
; 
; (def stars (future (->> "Assets/Hack/data/stars.edn"
; slurp
; edn/read-string
; (map #(hash-map
; :position (apply vector* ((juxt :x :y :z) %))
; :velocity (apply vector* ((juxt :x :y :z) %))
; :speed (% :speed)
; :hip (% :hip)))
; (map #(assoc %
; :name
; (or (@star-names (% :position))
; (str "HIP" (% :hip))))))))

(def planets (->> "Assets/Hack/data/exoplanets.edn"
                  slurp
                  edn/read-string
                  (map #(hash-map
                          :position (apply vector* ((juxt :x :y :z) %))
                          :name (% :name)))))


(comment (pprint (->> @stars (drop 590)
                      (take 20)))
  
  (doseq [star (take 100 @stars)]
    (-> (create-primitive :cube)
        (hydrate/populate! {:transform 
                            [{:position (star :position)
                              ; :local-scale (Vector3. 0.1 0.1 0.1)
                              }]})))
  
  (dorun (map destroy (objects-named "Cube"))))