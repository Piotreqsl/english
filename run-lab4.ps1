<#
run-lab4.ps1
Builds the project and runs org.example.lab4.GradesStats with any provided arguments (csv file path).
Usage:
  .\run-lab4.ps1 grades.csv
#>
param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]] $Args
)

if ($Args.Length -eq 0) {
    Write-Host "Usage: .\run-lab4.ps1 <csv-file>"
    exit 1
}

Write-Host "Building project..."
& .\gradlew.bat build
if ($LASTEXITCODE -ne 0) {
    Write-Error "Build failed (exit code $LASTEXITCODE)"
    exit $LASTEXITCODE
}

Write-Host "Running org.example.lab4.GradesStats with arguments: $($Args -join ' ' )"
# Run the class from compiled classes folder. Pass through any args.
& java -cp ".\build\classes\java\main" org.example.lab4.GradesStats @Args

exit $LASTEXITCODE

