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
    
    .state('crearCuenta',{
      url: '/crearCuenta',
      templateUrl: 'cuentas/formularioCuenta.html',
      controller: 'cuentaFormController'
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
        console.log('Ingreso a evento clienteDelete...');
        $scope.alerts = [
            { type: 'success', msg: 'Registro eliminado con éxito!' }
        ];
    });
    
    $scope.$on('clearAlerts', function () {
        console.log('Ingreso a evento clearAlerts...');
        $scope.alerts = [];
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
                                          clienteService,modalService,auditoriaService) {

  $scope.roles = [
      {idRol: '1',nombreRol:'Medico'},
      {idRol: '2',nombreRol:'Paciente'},
      {idRol: '3',nombreRol:'Admin'},
    ];

  /*
  $scope.items=[
    {idRol: '-1',nombreRol:'N/A'}
  ];*/
  $scope.items=[];

  $scope.toggleSelection = function(idRol){
    var index = $scope.items.indexOf(idRol);
    if( index > -1){ //Ya existe en los items seleccionados, >> se debe eliminar
      $scope.items.splice(index,1); //-- Se elimina el item del arreglo
    }else{ //Si no existen en el arreglo se adiciona
      $scope.items.push(idRol);
    }
  }

  $scope.testCheckbox = function(){
    if ($scope.items.length == 0){
        console.log('No hay items seleccionados ...');
    }else{
      var index= 0;
      var cadenaRoles ="";
      for (; index < $scope.items.length; index++){
        console.log($scope.items[index].idRol + ' -- ' + $scope.items[index].nombreRol);
        cadenaRoles = cadenaRoles + $scope.items[index].nombreRol + ";"
      }
      alert("Seleccionó: " + cadenaRoles);
    }
  }

  $scope.consultarSaldosCliente = function(id){
    console.log('Invocando consulta de saldos para Cliente:'+ id );
    clienteService.consultarSaldos({idCliente:id}).$promise.then(
        function (data){ //caso de exito
            console.log('Saldo total: $' + data.saldoTotal);

        },
        function(response){ //Caso de error
          console.log('Mensaje error:'+ response.data.message);
          console.log('Codigo error :' + response.data.code);
          console.log('Status:'+ response.status );
        }
    )};

  $scope.iniciarAuditoria = function(){
     console.log('Se inicia invocacion a Auditoria');
     auditoriaService.get().$promise.then(
     function (data){ //caso de exito
         console.log('Id Proceso :' + data.numeroProceso);
         alert("Se creo proceso de auditoria No." +  data.numeroProceso );
     },
     function(response){ //Caso de error
         console.log('Mensaje error:'+ response.data.message);
         console.log('Codigo error :' + response.data.code);
         console.log('Status:'+ response.status );
     }
  )};
});
