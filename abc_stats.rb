require 'json'

metres = {}
programs = {}
progcounts = {}
i = 0
Dir.glob("**/*.abc").each { |f|
	i += 1
	print(i) if i % 100 == 0
	prevline = ""
	progcount = 0
	File.open(f, "r").each_line { |line|
		if line.start_with?("M:") 
			metre = line[2..-1].strip
			metres[metre] = 0 if metres[metre].nil?
			metres[metre] += 1
		elsif line.start_with?("%%MIDI program") && prevline != line #program lines appear twice, in files by midi2abc
			program = line[("%%MIDI program".length)..-1].strip
			programs[program] = 0 if programs[program].nil?
			programs[program] += 1
			progcount += 1
		end
		prevline = line
	}
	progcounts[progcount] = 0 if progcounts[progcount].nil?
	progcounts[progcount] += 1
}

File.open("metres.json","w") do |f|
  f.write(metres.to_json)
end
File.open("programs.json","w") do |f|
  f.write(programs.to_json)
end
File.open("progcounts.json","w") do |f|
  f.write(progcounts.to_json)
end
