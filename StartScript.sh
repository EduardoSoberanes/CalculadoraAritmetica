#!/bin/bash

export DB_URL="jdbc:mysql://localhost:3306/calculadora_aritmetica"
export DB_USER="root"
export DB_PASSWORD="Contra1."
export JWT_SECRET="dGhpcy1pcy1hLXZlcnktc2VjdXJlLWtleS1mb3ItamN3dC1hdXRoZW50aWNhdGlvbi1pbi1teS1rYXJhdGUtYXBw"
export MAIL_API_KEY="152fddf485aa7b38a3163d49196f0eb1"
export PASSWORD_DEFAULT="RavenChallengeBackendDeveloper2026"

mvn clean install

java -jar target/CalculadoraAritmetica-0.0.1-SNAPSHOT.jar
