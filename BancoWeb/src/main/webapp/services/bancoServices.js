'use strict';

var myModule = angular.module('myApp.services', []);

// Service that provides persons operations
myModule.factory('clienteService', function ($resource) {
    return $resource('/Banco/api/clientes/:id');
});



