@echo off
setlocal enabledelayedexpansion

set "repoDir=%USERPROFILE%\.m2\repository\org\apache\maven"
set /a count = 0
for /r "%repoDir%" %%f in ("*.pom") do (
  set /a count += 1
)

echo !count! dependencies found.