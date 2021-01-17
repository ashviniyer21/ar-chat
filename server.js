const express = require("express");
const socketIO = require('socket.io');

const PORT = process.env.PORT || 3000;
const INDEX = '/client.html';

const server = express()
    .use((req, res) => res.sendFile(INDEX, { root: __dirname }))
    .listen(PORT, () => console.log(`Listening on ${PORT}`));

const io = socketIO(server);

let connections = new Map();

io.on('connection', (socket) => {
   socket.on('connection-attempt', (id) => {
       if(connections.get(id) === undefined){
           connections.set(id, 0);
       }
       if(connections.get(id) < 2){
           socket.join(id);
           connections.set(id, connections.get(id) + 1);
           if(connections.get(id) === 1){
               // console.log("joined");
               io.to(id).emit('connection-successful-1');
           } else {
               io.to(id).emit('connection-successful-2', id);
           }
       } else {
           io.to(socket.id).emit('room-full');
       }
   });
   socket.on('image', (id, imageString) => {
        // console.log("Send");
        socket.broadcast.emit('image2', imageString);
    });
   socket.on('disconnect', () => console.log('Client disconnected'));
});
setInterval(() => io.emit('time', new Date().toTimeString()), 1000);