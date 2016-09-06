Dir.glob("**/*.MID").each { |f|
	begin 
		puts(f)
		next if File.exist?("#{f}.abc")
		`midi2abc.exe \"#{f}\" -o \"#{f}.abc\"`
	rescue => e
		puts("***************ERROR***************")
		puts(e)
	end
}