/*
    name: index
    purpose: Just an entry point, the top module used to create the server
*/
var trafficHandler = require("./trafficHandler")
var constants = require("./constants");
var ip = require("ip");
var app = require('express');
var server = require('http').Server(app);
var io = require('socket.io')(server)

const PORT = 5000;
const IP_ADDRESS = ip.address()

/* Server is starting to listen to PORT, at IP_ADDRESS*/
server.listen(PORT, trafficHandler.clbkPrintNetworkInfo(PORT, IP_ADDRESS));


io.on(constants.CONNECTION, function(socket)
{
    trafficHandler.clbkConnectionEstablished(socket, io);
})