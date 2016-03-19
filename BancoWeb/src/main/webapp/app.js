'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'myApp.clientes'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider
    .when('/clientes', {
        templateUrl : 'clientes/clientes.html',
        controller  : 'clientesListController'
    })      
                
    .otherwise({redirectTo: '/view1'});
}]);
