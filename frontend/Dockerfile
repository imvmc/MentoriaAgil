# Estágio 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Instalar dependências
COPY package*.json ./
RUN npm ci

# Copiar código e gerar o build de produção
COPY . .
RUN npm run build -- --configuration=production

# Estágio 2: Serve (Nginx)
FROM nginx:alpine
# Copia o build do estágio anterior para o diretório do Nginx
# Ajuste 'mentoria-agil' se o nome do projeto no angular.json for diferente
COPY --from=build /app/dist/mentoria-agil/browser /usr/share/nginx/html

# Copia uma configuração customizada do Nginx para suportar rotas do Angular (SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
