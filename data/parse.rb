require "edn"

file = open("milkyway.spec").read
lines = file.split("\n")
data = lines.map { |l| l.strip.split.map { |s| s.to_f } }
hdata = data.map { |d| { x:d[0],
                         y:d[1],
                         z:d[2],
                         vx:d[9+3],
                         vy:d[10+3],
                         vz:d[11+3],
                         speed:d[12+3],
                         hip:d[13+3].to_i } }


p hdata.to_edn