require "edn"

def parse_stars
  file = open("stars.spec").read
  lines = file.split("\n")
  data = lines.map { |l| l.strip.split.map { |s| s.to_f } }
  data.map { |d| { x:d[0],
                   y:d[1],
                   z:d[2],
                   vx:d[9+3],
                   vy:d[10+3],
                   vz:d[11+3],
                   speed:d[12+3],
                   hip:d[13+3].to_i } }
end

def parse_star_names
  file = open("stars.label").read
  lines = file.split("\n")
  data = lines.map { |l| l.strip.split }
  data.map { |d| { x:d[0].to_f,
                   y:d[1].to_f,
                   z:d[2].to_f,
                   name:d[4..-1].join(" ") } }
end

def parse_exoplanets
  file = open("expl.label").read
  lines = file.split("\n")
  data = lines.map { |l| l.strip.split }
  data.map { |d| { x:d[0].to_f,
                   y:d[1].to_f,
                   z:d[2].to_f,
                   name:d[4..-1].join(" ") } }
end


puts parse_star_names.to_edn