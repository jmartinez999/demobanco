'use strict';
// Declare app level module which depends on views, and components
var app = angular.module('demobanco', ['ui.router','ngResource', 'ui.bootstrap','ngResource','ngGrid','app.utils']);

app.config(['$stateProvider','$urlRouterProvider', function($stateProvider,$urlRouterProvider) {
  $urlRouterProvider.otherwise("/home");
  
  $stateProvider.
    state('home',{
      url: '/home',
      templateUrl: 'home.html'
    })
      
    .state('clientes',{
      url:'/clientes',
      templateUrl: 'clientes/clientes.html',
      controller: 'clientesListController'
    })
  
    .state('crearCliente',{
      url: '/crearCliente',
      templateUrl: 'clientes/formularioCliente.html',
      controller: 'clienteFormController'
    })
    
    .state('modificarCliente',{
      url: '/modificarCliente',
      templateUrl: 'clientes/formularioCliente.html',
      controller: 'clienteFormController',
      params : {'idCliente':null}
    })

    .state('probarCosas',{
       url:'/probarCosas',
       templateUrl: 'test.html',
       controller: 'testController'
    });
    
}]);

// Create a controller with name alertMessagesController to bind to the feedback messages section.
app.controller('alertMessagesController', function ($scope) {
    // Picks up the event to display a saved message.
    $scope.$on('clienteSaved', function () {
        $scope.alerts = [
            { type: 'success', msg: 'Registro guadado con éxito!' }
        ];
    });

    // Picks up the event to display a deleted message.
    $scope.$on('clienteDeleted', function () {
        $scope.alerts = [
            { type: 'success', msg: 'Record eliminado con éxito!' }
        ];
    });

    // Picks up the event to display a server error message.
    $scope.$on('error', function (event,message) {

        if ( !message ){
          message = 'Error general de sistema, comuniquese con el area de soporte';
        }
        $scope.alerts = [
            { type: 'danger', msg: message }
        ];
    });

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };
});

app.controller('testController',function ($scope, $rootScope, $stateParams, $state,
                                          clienteService,modalService) {
  $scope.consultarSaldosCliente = function(id){

    console.log('Invocando consulta de saldos para Cliente:'+ id );
    clienteService.consultarSaldos({idCliente:id}).$promise.then(
        function (response){ //caso de exito
            console.log('Saldo total: $' + response.data.saldoTotal);

        },
        function(response){ //Caso de error
          console.log('Mensaje error:'+ response.data.message);
          console.log('Codigo error :' + response.data.code);
          console.log('Status:'+ response.status );
        }
    )};
});
