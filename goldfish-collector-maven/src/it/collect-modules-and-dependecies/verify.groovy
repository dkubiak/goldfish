def found=false
new File(basedir, "build.log").eachLine {
    line ->
    if (line.contains("Goldfish plugin finish with SUCCESS!")) {
        found=true
    }
}
assert found:"Plugin should finish without errors: but it failed."