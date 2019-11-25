FROM node:8-alpine
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Install app dependencies
COPY package.json /usr/src/app/
# RUN rm -rf node_modules package-lock.json && npm cache clear --force
# RUN npm uninstall *
RUN npm install

# Bundle app source
COPY . /usr/src/app

EXPOSE 8080
CMD [ "node", "server.js" ]