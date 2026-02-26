# Estágio 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Instalar dependências
COPY package*.json ./
RUN npm ci

# Copiar código e gerar o build de produção
COPY angular.json .
COPY tsconfig*.json .
COPY src ./src
RUN npm run build -- --configuration=production

# Estágio 2: Serve (Nginx)
FROM nginx:alpine

# Criar usuário não-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar build
COPY --from=build /app/dist/mentoria-agil/browser /usr/share/nginx/html

# Copiar config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Ajustar permissões
RUN chown -R appuser:appgroup /usr/share/nginx/html

# Trocar para usuário não-root
USER appuser

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
