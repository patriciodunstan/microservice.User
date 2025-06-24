# Microservicio de Autenticación de Usuarios

Un microservicio completo de Spring Boot para registro, login y autenticación de usuarios con JWT, incluyendo pipeline de CI/CD con seguridad.

## 🚀 Características

- ✅ Registro de usuarios con validación
- ✅ Login con autenticación JWT
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Validación de datos con Bean Validation
- ✅ Base de datos MongoDB
- ✅ Tests unitarios con Mockito y JUnit
- ✅ Tests de integración
- ✅ Manejo global de excepciones
- ✅ **Pipeline CI/CD con GitHub Actions**
- ✅ **Escaneo de seguridad con OWASP Dependency Check**
- ✅ **Escaneo de vulnerabilidades con Trivy**
- ✅ **Deploy automático**

## 🛠️ Tecnologías

- **Spring Boot 3.4.1**
- **Spring Security**
- **Spring Data MongoDB**
- **JWT (JSON Web Tokens)**
- **Lombok**
- **JUnit 5 + Mockito**
- **Maven**
- **GitHub Actions**
- **OWASP Dependency Check**
- **Trivy**

## 📋 Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- MongoDB (local o remoto)
- Docker (para CI/CD)

## 🔧 Configuración

### 1. Clonar el proyecto
```bash
git clone <repository-url>
cd microservice.User
```

### 2. Configurar MongoDB
Asegúrate de que MongoDB esté ejecutándose en tu máquina local:
```bash
# En Windows
mongod

# En macOS/Linux
sudo systemctl start mongod

# Con Docker
docker run -d -p 27017:27017 --name mongodb mongo:6.0
```

### 3. Configuración de la aplicación
Edita `src/main/resources/application.properties` según tu configuración de MongoDB:

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=userdb
```

## 🚀 Ejecución

### Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

**⚠️ Nota**: Para ejecutar la aplicación completa necesitas MongoDB. Para desarrollo y testing, puedes usar solo los tests unitarios.

### Ejecutar tests (Sin base de datos)
```bash
# Todos los tests unitarios (no requieren MongoDB)
mvn test

# Solo tests de servicio
mvn test -Dtest="*ServiceTest"

# Solo tests de controlador
mvn test -Dtest="*ControllerTest"

# Solo tests de configuración
mvn test -Dtest="*ConfigTest"

# Tests de JWT
mvn test -Dtest="*JwtServiceTest"
```

### Ejecutar escaneo de seguridad
```bash
# OWASP Dependency Check
mvn dependency-check:check

# Ver reporte HTML
open target/security-reports/dependency-check-report.html
```

## 🔒 Pipeline de Seguridad y CI/CD

### GitHub Actions Workflow

El proyecto incluye un pipeline completo de CI/CD que se ejecuta automáticamente en:

- **Push a main/develop**
- **Pull Requests a main/develop**

### Configuración de Secrets

**⚠️ IMPORTANTE**: Antes de usar el pipeline, debes configurar los GitHub Secrets.

1. Ve a tu repositorio en GitHub
2. **Settings** → **Secrets and variables** → **Actions**
3. Agrega los secrets necesarios

#### Secrets Mínimos Requeridos:
- `JWT_SECRET`: Clave secreta para JWT (mínimo 64 caracteres)

#### Secrets Opcionales (según tu configuración):
- Credenciales de base de datos (`MONGODB_*`)
- Credenciales de cloud (AWS, GCP, Azure)
- Notificaciones (Slack, Teams)

**📖 Ver [GITHUB_SECRETS.md](GITHUB_SECRETS.md) para la lista completa y ejemplos.**

### Jobs del Pipeline

1. **Security Scan**
   - OWASP Dependency Check
   - Escaneo de vulnerabilidades en dependencias
   - Genera reportes HTML y JSON

2. **Test and Build**
   - Tests unitarios
   - Tests de integración (con MongoDB en Docker)
   - Build de la aplicación
   - Subida de artefactos

3. **Docker Security Scan**
   - Construcción de imagen Docker
   - Escaneo con Trivy
   - Subida de resultados a GitHub Security

4. **Deploy** (solo en main)
   - Deploy automático a producción
   - Configurable para diferentes plataformas

5. **Notify**
   - Notificaciones de éxito/fallo
   - Configurable para Slack, Teams, etc.

### Configuración del Pipeline

El pipeline está configurado en `.github/workflows/ci.yml` y incluye:

- **Caché de Maven** para builds más rápidos
- **MongoDB como servicio** para tests de integración
- **Escaneo de seguridad** con OWASP y Trivy
- **Artefactos** para reportes y builds
- **Deploy condicional** solo en main
- **Variables de entorno** para datos sensibles

### Personalización del Deploy

Para configurar el deploy automático, edita el job `deploy` en `.github/workflows/ci.yml` y configura los secrets correspondientes:

```yaml
# El pipeline detecta automáticamente qué plataforma usar basado en los secrets disponibles
- name: Deploy to production
  run: |
    # AWS ECS (si AWS_ACCESS_KEY_ID está configurado)
    if [ -n "${{ secrets.AWS_ACCESS_KEY_ID }}" ]; then
      aws ecs update-service --cluster ${{ secrets.AWS_ECS_CLUSTER }} --service ${{ secrets.AWS_ECS_SERVICE }}
    fi
    
    # Google Cloud Run (si GCP_SA_KEY está configurado)
    if [ -n "${{ secrets.GCP_SA_KEY }}" ]; then
      gcloud run deploy microservice-user --image gcr.io/${{ secrets.GCP_PROJECT_ID }}/microservice-user
    fi
```

## 📚 API Endpoints

### 1. Registro de Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "usuario@ejemplo.com",
  "username": "usuario123",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez"
}
```

**Respuesta exitosa (201):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "1",
    "email": "usuario@ejemplo.com",
    "username": "usuario123",
    "firstName": "Juan",
    "lastName": "Pérez",
    "role": "USER"
  }
}
```

### 2. Login de Usuario
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@ejemplo.com",
  "password": "password123"
}
```

**Respuesta exitosa (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "1",
    "email": "usuario@ejemplo.com",
    "username": "usuario123",
    "firstName": "Juan",
    "lastName": "Pérez",
    "role": "USER"
  }
}
```

### 3. Health Check
```http
GET /api/auth/health
```

**Respuesta (200):**
```
Auth Service is running!
```

## 🧪 Tests

### Estructura de Tests

```
src/test/java/
├── AuthControllerTest.java        # Tests del controlador (MockMvc)
├── AuthServiceTest.java           # Tests del servicio (Mockito)
├── JwtServiceTest.java            # Tests del servicio JWT
└── config/
    └── SecurityConfigTest.java    # Tests de configuración de seguridad
```

### Ejecución de Tests

```bash
# Tests unitarios (no requieren MongoDB)
mvn test

# Tests específicos
mvn test -Dtest="*ServiceTest"     # Solo servicios
mvn test -Dtest="*ControllerTest"  # Solo controladores
mvn test -Dtest="*ConfigTest"      # Solo configuración
```

### Configuración de Tests

- **Tests unitarios**: Se ejecutan sin dependencias externas usando mocks
- **Tests de configuración**: Verifican componentes individuales sin contexto completo
- **Sin base de datos**: Todos los tests funcionan sin MongoDB
- **Sin contexto Spring**: Tests simples y rápidos

### Cobertura de Tests

Los tests cubren:
- ✅ Registro de usuarios (éxito y errores)
- ✅ Login de usuarios (éxito y errores)
- ✅ Validación de datos
- ✅ Manejo de excepciones
- ✅ Generación y validación de JWT
- ✅ Configuración de seguridad
- ✅ Caracteres especiales y casos edge

## 🔒 Seguridad

### Escaneo de Vulnerabilidades

- **OWASP Dependency Check**: Escanea dependencias Maven
- **Trivy**: Escanea imagen Docker
- **Configuración**: Falla build si CVSS > 7

### Configuración de Seguridad

- **Encriptación de contraseñas**: BCrypt
- **Autenticación**: JWT (JSON Web Tokens)
- **Validación**: Bean Validation
- **CORS**: Configurado para permitir todas las origenes
- **CSRF**: Deshabilitado para API REST
- **Usuario no-root**: En contenedor Docker

## 📁 Estructura del Proyecto

```
├── .github/
│   └── workflows/
│       └── ci.yml                 # Pipeline CI/CD
├── src/
│   ├── main/java/
│   │   └── com/userREgisterLoginAuth/microservice/User/
│   │       ├── Application.java
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── repository/
│   │       └── service/
│   └── test/
│       ├── java/
│       └── resources/
│           ├── application-test.properties
│           └── application-ci.properties
├── Dockerfile                     # Imagen Docker
├── pom.xml                       # Dependencias y plugins
└── README.md                     # Documentación
```

## 🐛 Troubleshooting

### Error de conexión a MongoDB
```
Connection refused to localhost:27017
```
**Solución**: Asegúrate de que MongoDB esté ejecutándose.

### Error en CI/CD
```
MongoDB connection failed in CI
```
**Solución**: El pipeline usa MongoDB en Docker automáticamente.

### Error de seguridad
```
OWASP Dependency Check failed
```
**Solución**: Revisa el reporte en `target/security-reports/` y actualiza dependencias vulnerables.

### Error de Trivy
```
Trivy found vulnerabilities
```
**Solución**: Revisa el reporte SARIF y actualiza la imagen base o dependencias.

## 📝 Licencia

Este proyecto está bajo la Licencia MIT.

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

**Nota**: El pipeline de CI/CD se ejecutará automáticamente en tu PR.

## 📞 Contacto

Para preguntas o soporte, contacta a: [tu-email@ejemplo.com] 