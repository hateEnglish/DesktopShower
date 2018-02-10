@echo off

for %%f in (./src/main/resources/proto/*.proto) do protoc --java_out=./src/main/java/ ./src/main/resources/proto/%%f

pause