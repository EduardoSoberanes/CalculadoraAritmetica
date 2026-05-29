0000000000000# CalculadoraAritmetica

Este proyecto es una API RESTful para realizar operaciones aritméticas básicas y mantener un historial de las mismas, con autenticación de usuarios y validación de emails.

## 🚀 API Externa Utilizada

### Mailboxlayer
*   **Razón de la elección**: Se eligió Mailboxlayer para la validación de correos electrónicos debido a su facilidad de integración y su capacidad para verificar la validez del formato, la existencia del dominio (MX records) y si el correo es desechable. Esto es crucial para mantener la calidad de los datos de usuario y prevenir registros con emails temporales o inválidos.

### Ejemplo de Configuración de API Key
La API Key para Mailboxlayer se configura en el archivo `src/main/resources/application.properties`:

```properties
api.mailboxlayer.key = TU_API_KEY_DE_MAILBOXLAYER
```
**Importante**: Reemplaza `TU_API_KEY_DE_MAILBOXLAYER` con tu clave real obtenida de Mailboxlayer. Para entornos de producción, considera usar variables de entorno o un sistema de gestión de secretos.

### Lógica Aplicada para Determinar si un Email es Aceptado
La lógica para aceptar un email se encuentra en el servicio `EmailValidationService` y se basa en los siguientes criterios de la respuesta de Mailboxlayer:
1.  **`format_valid`**: Debe ser `true` (el formato del email es correcto).
2.  **`mx_found`**: Debe ser `true` (se encontraron registros MX para el dominio, indicando que el dominio existe y puede recibir correos).
3.  **`disposable`**: Debe ser `false` (el email no es de un proveedor de correos temporales/desechables).

Si alguna de estas condiciones no se cumple, el email es considerado inválido y no se permite el registro del usuario.

## 🛠️ Instrucciones de Instalación

Para levantar el proyecto localmente, sigue estos pasos:

1.  **Clonar el repositorio**:  
    `git clone git@github.com:EduardoSoberanes/CalculadoraAritmetica.git`

2.  **Requisitos previos**:
    *   Java 11 o superior
    *   Maven 3.x
    *   Una base de datos MySQL (o configurar otra base de datos compatible con JPA)

3.  **Configurar la base de datos y la API externa**:  
    Abre el archivo `src/main/resources/application.properties` y ajusta las siguientes propiedades:  

    ```properties
    spring.application.name=CalculadoraAritmetica
    logging.level.org.springframework.security=DEBUG
    
    # Configuración de la base de datos MySQL
    spring.datasource.url=${DB_URL}
    spring.datasource.username=${DB_USER}
    spring.datasource.password=${DB_PASSWORD}
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
    spring.jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=update 

    # Configuración de seguridad (JWT)
    application.security.expiration=10800000
    application.security.secret-key=${JWT_SECRET}
    application.security.password-default=${PASSWORD_DEFAULT}

    # Configuración de la API externa Mailboxlayer
    api.mailboxlayer.key=${MAIL_API_KEY}
    ```
    **Nota**: Asegúrate de que tu base de datos MySQL esté corriendo y que las credenciales sean correctas (configure las variables de entorno en su sistema para poder acceder a ellas). El valor `ddl-auto=update` creará las tablas necesarias si no existen.

      
4.  **Compilar y ejecutar el proyecto**:  
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    La aplicación estará disponible en `http://localhost:8080`.

## 🚀 Ejemplos de Uso (con `curl`)

Primero, necesitarás registrar un usuario y luego autenticarte para obtener un token JWT.

### 1. Registro de Usuario (POST /api/auth/register)

```bash
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}'
```
**Respuesta esperada (ejemplo):**
```json
{
  "message": "You have successfully registered!",
  "usuarioDTO": {
    "username": "testuser",
    "email": "test@example.com",
    "createAt": "2023-10-27T10:00:00.000+00:00"
  }
}
```

### 2. Autenticación de Usuario (POST /api/auth/authenticate)
Usa las credenciales del usuario registrado para obtener un token JWT.

```bash
curl -X POST http://localhost:8080/api/auth/authenticate \
-H "Content-Type: application/json" \
-d '{
  "email": "test@example.com",
  "password": "password123"
}'
```
**Respuesta esperada (ejemplo):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjk4NDAwMDAwLCJleHAiOjE2OTg0MTA4MDB9...."
}
```
**Guarda este token**, lo necesitarás para las siguientes peticiones.

### 3. Realizar una Operación Aritmética (POST /api/calculate)
Envía una operación y los operandos. Necesitas el token JWT en el encabezado `Authorization`.

```bash
curl -X POST http://localhost:8080/api/calculate \
-H "Content-Type: application/json" \
-H "Authorization: Bearer TU_TOKEN_JWT" \
-d '{
  "operation": "ADD",
  "operandA": 10,
  "operandB": 5
}'
```
**Respuesta esperada (ejemplo):**
```json
{
  "id": 1,
  "usuario": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "createAt": "2023-10-27T10:00:00.000+00:00"
  },
  "operation": "ADD",
  "operandA": 10,
  "operandB": 5,
  "result": 15,
  "timestamp": "2023-10-27T10:05:00.000+00:00"
}
```
**Operaciones disponibles**: `ADD`, `SUBSTRACT`, `MULTIPLY`, `DIVIDE`, `MODULO`, `POW`.

### 4. Obtener Historial de Operaciones (GET /api/history)
Obtén el historial de operaciones del usuario autenticado, con paginación y ordenamiento.

```bash
curl -X GET "http://localhost:8080/api/history?page=0&size=5&sortBy=timestamp&direction=desc" \
-H "Authorization: Bearer TU_TOKEN_JWT"
```
**Respuesta esperada (ejemplo):**
```json
[
  {
    "id": 1,
    "operationEnum": "ADD",
    "operandA": 10,
    "operandB": 5,
    "result": 15,
    "timestamp": "2023-10-27T10:05:00.000+00:00"
  },
  {
    "id": 2,
    "operationEnum": "MULTIPLY",
    "operandA": 2,
    "operandB": 3,
    "result": 6,
    "timestamp": "2023-10-27T10:04:00.000+00:00"
  }
]
```

### 5. Eliminación de Operaciones (DELETE /api/history/{id})
Elimina una operación del usuario autenticado.

```bash
curl --location --request DELETE 'localhost:8080/api/history/5' \
--header 'Authorization: Bearer TU_TOKEN_JWT'
```

**Respuesta esperada:**  
Obtendremos un status `204 No Content` si la operación fue exitosa.

## 💡 Decisiones Técnicas Tomadas

Aquí se detallan algunas de las decisiones técnicas clave y las razones detrás de ellas:

*   **Spring Boot**: Framework principal para el desarrollo de la API REST.
    *   **Razón**: Facilita el desarrollo rápido de aplicaciones stand-alone, con un ecosistema robusto y una gran comunidad. Proporciona auto-configuración y un servidor embebido, simplificando el despliegue.

*   **Spring Security con JWT (JSON Web Tokens)**: Para la autenticación y autorización de usuarios.
    *   **Razón**: JWTs son ideales para APIs REST sin estado, permitiendo una autenticación segura y escalable sin necesidad de sesiones en el servidor. Se utiliza para proteger los endpoints y asegurar que solo usuarios autenticados puedan realizar operaciones.

*   **Spring Data JPA / Hibernate**: Para la persistencia de datos y la interacción con la base de datos.
    *   **Razón**: Abstracción de la capa de persistencia, lo que permite trabajar con objetos Java en lugar de SQL directamente y facilita el cambio de base de datos si fuera necesario. Simplifica la implementación de repositorios y operaciones CRUD.

*   **Maven**: Herramienta de gestión de proyectos y construcción.
    *   **Razón**: Estándar en proyectos Java, facilita la gestión de dependencias, la compilación, las pruebas y el empaquetado del proyecto de manera estandarizada.

*   **Mailboxlayer (integrado con OpenFeign)**: Integración con una API externa para la validación de emails.
    *   **Razón**: Asegura la calidad de los datos de registro al verificar el formato, la existencia del dominio y si el email es desechable, previniendo spam o registros fraudulentos. Se usó **OpenFeign** para una integración declarativa y sencilla de clientes HTTP, lo que reduce el código boilerplate y mejora la legibilidad.

*   **Paginación y Ordenamiento con Spring Data `Pageable`**: Implementación de `Pageable` en el endpoint `/api/history`.
    *   **Razón**: Mejora la eficiencia y la experiencia del usuario al manejar grandes volúmenes de datos, evitando cargar todo el historial de una vez y permitiendo flexibilidad en la visualización (número de elementos por página, orden ascendente/descendente por cualquier campo).

*   **Manejo Global de Excepciones (`@RestControllerAdvice`)**: Centralización del manejo de errores.
    *   **Razón**: Proporciona respuestas de error consistentes y amigables al cliente (utilizando `ErrorDTO`), desacoplando la lógica de manejo de errores de los controladores individuales y mejorando la mantenibilidad del código.

*   **`BigDecimal` para Operaciones Aritméticas**: Uso de `BigDecimal` para los operandos y resultados.
    *   **Razón**: Evita problemas de precisión inherentes a los tipos de punto flotante (`float`, `double`) en Java, lo cual es crítico en aplicaciones que manejan cálculos financieros o que requieren alta exactitud.

*   **Validación de Rango de Operandos**: Se valida que los operandos estén dentro de un rango específico (-1,000,000 a 1,000,000).
    *   **Razón**: Previene desbordamientos o cálculos excesivamente grandes que podrían afectar el rendimiento o la estabilidad de la aplicación, además de controlar los límites de la lógica de negocio.

*   **JUnit 5 y Mockito**: Para las pruebas unitarias.
    *   **Razón**: Herramientas estándar y potentes en el ecosistema Java para asegurar la calidad del código. **JUnit 5** proporciona un framework moderno para escribir pruebas, mientras que **Mockito** permite simular dependencias, facilitando el aislamiento de las unidades de código bajo prueba.
