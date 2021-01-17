const express = require("express");
const socketIO = require('socket.io');

const PORT = process.env.PORT || 3000;
const INDEX = '/client2.html';

const app = express();
// server.use((req, res) => res.sendFile(INDEX, { root: __dirname }))
//     .listen(PORT, () => console.log(`Listening on ${PORT}`));
app.use(express.static(__dirname));
const http = require('http').Server(app);
const io = socketIO(http);

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
   socket.on('connection-attempt-2', (id) => {
       if(connections.get(id) === undefined){
           connections.set(id, 0);
       }
       if(connections.get(id) < 4){
           socket.join(id);
           connections.set(id, connections.get(id) + 1);
           if(connections.get(id) === 1){
               // console.log("joined");
               io.to(id).emit('connection-successful-1');
           } else if(connections.get(id) === 2){
               io.to(id).emit('connection-successful-2');
           } else if(connections.get(id) === 3){
               io.to(id).emit('connection-successful-3');
           } else {
               io.to(id).emit('connection-successful-4', id);
           }
       } else {
           io.to(socket.id).emit('room-full');
       }
   })
   socket.on('image', (imageString) => {
        // console.log("Send");
        socket.broadcast.emit('image2', imageString);
    });
   socket.on('image3', (first, imageString) => {
       socket.broadcast.emit('image4', first, imageString);
   })
   socket.on('disconnect', () => console.log('Client disconnected'));
});

http.listen(PORT, () => {
    console.log('listening on *:' + PORT);
});

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/client2.html');
});