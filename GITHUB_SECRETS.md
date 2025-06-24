# GitHub Secrets Configuration

Este documento explica todos los GitHub Secrets necesarios para el pipeline de CI/CD.

## 🔐 Cómo configurar GitHub Secrets

1. Ve a tu repositorio en GitHub
2. Click en **Settings** → **Secrets and variables** → **Actions**
3. Click en **New repository secret**
4. Agrega cada secret con su nombre y valor

## 📋 Secrets Requeridos

### 🔒 Seguridad y Autenticación

| Secret Name | Descripción | Ejemplo |
|-------------|-------------|---------|
| `JWT_SECRET` | Clave secreta para firmar JWT tokens | `mySuperSecretKey123456789012345678901234567890123456789012345678901234567890` |
| `JWT_EXPIRATION` | Tiempo de expiración de JWT en milisegundos | `86400000` (24 horas) |

### 🗄️ Base de Datos (Opcional - para producción)

| Secret Name | Descripción | Ejemplo |
|-------------|-------------|---------|
| `MONGODB_HOST` | Host de MongoDB | `your-mongodb-cluster.mongodb.net` |
| `MONGODB_PORT` | Puerto de MongoDB | `27017` |
| `MONGODB_DATABASE` | Nombre de la base de datos | `userdb_prod` |
| `MONGODB_USERNAME` | Usuario de MongoDB | `app_user` |
| `MONGODB_PASSWORD` | Contraseña de MongoDB | `your_secure_password` |

### ☁️ AWS (Para deploy a AWS)

| Secret Name | Descripción | Ejemplo |
|-------------|-------------|---------|
| `AWS_ACCESS_KEY_ID` | AWS Access Key ID | `AKIAIOSFODNN7EXAMPLE` |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Access Key | `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` |
| `AWS_REGION` | Región de AWS | `us-east-1` |
| `AWS_ECS_CLUSTER` | Nombre del cluster ECS | `my-app-cluster` |
| `AWS_ECS_SERVICE` | Nombre del servicio ECS | `microservice-user` |

### ☁️ Google Cloud Platform (Para deploy a GCP)

| Secret Name | Descripción | Ejemplo |
|-------------|-------------|---------|
| `GCP_SA_KEY` | Service Account Key JSON | `{"type": "service_account", "project_id": "..."}` |
| `GCP_PROJECT_ID` | ID del proyecto GCP | `my-project-123456` |
| `GCP_REGION` | Región de GCP | `us-central1` |

### ☁️ Azure (Para deploy a Azure)

| Secret Name | Descripción | Ejemplo |
|-------------|-------------|---------|
| `AZURE_CREDENTIALS` | Credenciales de Azure | `{"clientId": "...", "clientSecret": "..."}` |
| `AZURE_RESOURCE_GROUP` | Grupo de recursos | `my-resource-group` |
| `AZURE_IMAGE` | URL de la imagen de contenedor | `myacr.azurecr.io/microservice-user:latest` |

### 🐳 Docker Hub (Para push de imágenes)

| Secret Name | Descripción | Ejemplo |
|-------------|-------------|---------|
| `DOCKERHUB_USERNAME` | Usuario de Docker Hub | `myusername` |
| `DOCKERHUB_TOKEN` | Token de acceso de Docker Hub | `dckr_pat_...` |

### 📢 Notificaciones (Opcional)

| Secret Name | Descripción | Ejemplo |
|-------------|-------------|---------|
| `SLACK_WEBHOOK_URL` | URL del webhook de Slack | `https://hooks.slack.com/services/T00000000/B00000000/...` |
| `TEAMS_WEBHOOK_URL` | URL del webhook de Microsoft Teams | `https://outlook.office.com/webhook/...` |

## 🚀 Configuración por Entorno

### Desarrollo Local
No necesitas configurar secrets. La aplicación usa valores por defecto.

### CI/CD (GitHub Actions)
- **Obligatorios**: `JWT_SECRET`
- **Opcionales**: Todos los demás según tu configuración de deploy

### Producción
- **Obligatorios**: `JWT_SECRET`, credenciales de la plataforma de deploy
- **Recomendados**: Credenciales de base de datos, notificaciones

## 🔧 Ejemplos de Configuración

### Configuración Mínima
```bash
JWT_SECRET=mySuperSecretKey123456789012345678901234567890123456789012345678901234567890
```

### Configuración Completa para AWS
```bash
JWT_SECRET=mySuperSecretKey123456789012345678901234567890123456789012345678901234567890
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_REGION=us-east-1
AWS_ECS_CLUSTER=my-app-cluster
AWS_ECS_SERVICE=microservice-user
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/T00000000/B00000000/...
```

### Configuración para Google Cloud
```bash
JWT_SECRET=mySuperSecretKey123456789012345678901234567890123456789012345678901234567890
GCP_SA_KEY={"type": "service_account", "project_id": "my-project-123456", ...}
GCP_PROJECT_ID=my-project-123456
GCP_REGION=us-central1
```

## ⚠️ Seguridad

### ✅ Buenas Prácticas
- Usa claves JWT largas y aleatorias (mínimo 64 caracteres)
- Rota las claves regularmente
- Usa Service Accounts en lugar de credenciales de usuario
- Limita los permisos de las credenciales al mínimo necesario
- Usa tokens de acceso en lugar de contraseñas cuando sea posible

### ❌ Evitar
- No uses la misma clave JWT en desarrollo y producción
- No uses credenciales de usuario para CI/CD
- No uses claves débiles o predecibles
- No compartas secrets en logs o código

## 🔄 Rotación de Secrets

### JWT Secret
1. Genera una nueva clave JWT
2. Actualiza el secret en GitHub
3. Reinicia la aplicación
4. Los tokens existentes seguirán funcionando hasta que expiren

### Credenciales de Cloud
1. Crea nuevas credenciales
2. Actualiza los secrets en GitHub
3. El deploy automático usará las nuevas credenciales

## 📞 Soporte

Si tienes problemas con la configuración de secrets:
1. Verifica que los nombres coincidan exactamente
2. Asegúrate de que los valores no tengan espacios extra
3. Revisa los logs del pipeline para errores específicos
4. Contacta al equipo de DevOps 