(ns universe.state
  (:use [universe.linear])
  (:require [data.core :as d]
            [clojure.edn :as edn])
  (:import [UnityEngine Vector3]))

(defn ^Double vdist [v1 v2]
  (.magnitude (v- v1 v2)))

(defn merge-similar-planets [ps]
  ps
  #_(loop [res [], ps (set ps)]
    (if-let [psq (seq ps)]
      (let [p (first psq)
            ppos (:position p)]
        (recur
          (conj res p)
          (reduce dissoc ps
            (filter
              #(< 1 (vdist ppos (:position %))) 
              psq))))
      res)))

(def standard-frequency 3)

(defn process-planets [planets]
  (merge-similar-planets
    (for [{:keys [x y z] :as p} planets]
      (-> p
        (dissoc :x :y :z)
        (assoc
          :position (v3 x y z)
          :signals []
          :frequency standard-frequency)))))

(def planets
  (process-planets
    (edn/read-string
      (slurp "Assets/hacktheuniverse/data/exoplanets.edn"))))

(def initial-state
  {:planets planets
   :time 0})

(def state
  (atom initial-state))

(def sigmax 20.0)

(defn planet-signal [planet r]
  {:radius r
   :civ (:civ planet)
   :signature (:signature planet)
   :max-radius sigmax})

(defn live-signal? [{:keys [radius max-radius]}]
  (< radius max-radius))

;; need to think more about how to do this, time-wise

(defn new-signals [planet prev-t t]
  (let [{:keys [frequency] :as planet} planet
        nsgs (- (int (/ t frequency))
               (int (/ prev-t frequency)))]
    (if (zero? nsgs)
      nil
      [(planet-signal planet 0)])))

(def lightspeed 1)

(defn advance-signal [{r :radius :as sgnl} t]
  (assoc sgnl :radius
    (+ r (* lightspeed t))))

(defn advance-signals [p prev-world t]
  (->> (:signals p)
    (map #(advance-signal % t))
    (filter live-signal?)
    (concat (new-signals p (:time prev-world) t))
    vec))

;; can optimize this further
(defn current-signal [sending-p receiving-p]
  (let [spos   (:position sending-p)
        rpos   (:position receiving-p)
        srdist (vdist rpos spos)]
    (->> (:signals sending-p)
      (filter #(< srdist (:radius %)))
      first)))

(defn signals-on-planet [p world]
  (->> (:planets world)
    (remove #(= p %))
    (keep #(current-signal % p))))

(def cultural-viscosity 0.95)

(defn advance-signature* [sig1 sig2]
  (v+ (vmult-scalar sig1 cultural-viscosity)
    (vmult-scalar sig2 (- 1 cultural-viscosity))))

(defn advance-signature [{:keys [signature] :as planet} signals]
  (reduce advance-signature* signature
    (map :signature signals)))

(defn advance-civ [pciv signals signature]
  (reduce
    (fn [pciv nciv]
      (if (< (vdist signature nciv)
            (vdist signature pciv))
        nciv
        pciv))
    pciv
    (map :civ signals)))

(defn advance-planet [p prev-world t]
  (let [sops    (signals-on-planet p prev-world)
        nsig    (advance-signature p sops)
        nciv    (if-let [c (:civ p)]
                  (advance-civ c sops nsig)
                  nil)
        signals (advance-signals p prev-world t)]
    (assoc p
      :signals signals
      :civ nciv
      :signature nsig)))

(defn advance-planets [prev-world t]
  (for [p (:planets prev-world)]
    (advance-planet p prev-world t)))

(defn advance-world [prev-world, t]
  (let [ps2   (advance-planets prev-world t)]
    (assoc prev-world
      :planets ps2
      :time t)))
