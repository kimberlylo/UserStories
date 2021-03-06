FROM node:latest

RUN mkdir parse

ADD . /parse
WORKDIR /parse
RUN npm install

ENV APP_ID final-jumble-id
ENV MASTER_KEY final-jumble-key
ENV DATABASE_URI mongodb://heroku_jk2jb9x2:n84fnp51qk6csd18rpjm46ajtu@ds119732.mlab.com:19732/heroku_jk2jb9x2

ENV FCM_API_KEY AAAAto1Azj4:APA91bEEDuiH6lHJyX4fsOn9RZfIW5f5UvizIl0NZeJV-m2y7fBdgUJiOPQC-FPsBwryzYvuo6RmpOx65X3q_KPUwPRR_N_E5HEa27enEms38Q08_AqtmBVYS366UGn5fg4iVrmQD0Zc9CxxwI5b-Z3zRut0mFn3lQ
ENV SERVER_URL https://final-jumble.herokuapp.com/parse
ENV SENDER_ID 784053882430

# Optional (default : 'parse/cloud/main.js')
ENV CLOUD_CODE_MAIN cloud/main.js

# Optional (default : '/parse')
# ENV PARSE_MOUNT mountPath

EXPOSE 1337

# Uncomment if you want to access cloud code outside of your container
# A main.js file must be present, if not Parse will not start

# VOLUME /parse/cloud               

CMD [ "npm", "start" ]
