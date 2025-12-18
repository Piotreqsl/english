<#
run-lab3.ps1
Builds the project and runs org.example.lab3.Main with any provided arguments.
Usage:
  .\run-lab3.ps1 37
  .\run-lab3.ps1 42
#>
param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]] $Args
)

Write-Host "Building project..."
& .\gradlew.bat build
if ($LASTEXITCODE -ne 0) {
    Write-Error "Build failed (exit code $LASTEXITCODE)"
    exit $LASTEXITCODE
}

Write-Host "Running org.example.lab3.Main with arguments: $($Args -join ' ' )"
# Run the class from compiled classes folder. Pass through any args.
& java -cp ".\build\classes\java\main" org.example.lab3.Main @Args

exit $LASTEXITCODE

