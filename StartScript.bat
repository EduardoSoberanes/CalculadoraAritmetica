@echo off

set DB_URL=jdbc:mysql://localhost:3306/calculadora_aritmetica
set DB_USER=root
set DB_PASSWORD=Contra1.
set JWT_SECRET=dGhpcy1pcy1hLXZlcnktc2VjdXJlLWtleS1mb3ItamN3dC1hdXRoZW50aWNhdGlvbi1pbi1teS1rYXJhdGUtYXBw
set MAIL_API_KEY=152fddf485aa7b38a3163d49196f0eb1
set PASSWORD_DEFAULT=RavenChallengeBackendDeveloper2026

mvn clean install

java -jar target\CalculadoraAritmetica-0.0.1-SNAPSHOT.jar