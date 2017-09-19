'use strict';
var app = angular.module('demobanco');
// Create a controller with name personsFormController to bind to the form section.
app.controller('cuentaFormController', function ($scope, $rootScope, $state,
          cuentaService,modalService,clienteService) {
  
  $scope.cliente={};
  $scope.nuevaCuenta={};//Datos de entrada para crear la cuenta
  $scope.cuenta ={}; // Resultado despues de invocar servicio de crear cuenta
  
  
  //TODO Reemplazar por consulta de items asociados a la enumeracion Java
  $scope.listaTiposIdentificacion = [
      {id: 'CC', name: 'Cedula'},
      {id: 'TI', name: 'T. Identidad'},
      {id: 'NIT', name: 'NIT'}
    ];
    
  
    
    /* No es eficiente porque se invoca N veces
    $scope.$watch('nuevaCuenta.numIdCliente', function() {
        console.log('El numero de identificacion cambio');
        if(!$scope.nuevaCuenta.numIdCliente )
          $scope.cliente.nombreCliente = '';
        else
           $scope.cliente.nombreCliente = 'Pepito Perez'; 
    });
    */
    
  $scope.buscarCliente = function(){
    if( $scope.nuevaCuenta.tipoIDCliente && $scope.nuevaCuenta.numIdCliente){
      clienteService.buscarCliente({tipoID:$scope.nuevaCuenta.tipoIDCliente,
          numeroID:$scope.nuevaCuenta.numIdCliente},
        function(result){
          $scope.cliente = result;
          if ($scope.cliente && $scope.cliente.nombre){
            console.log('Cliente encontrado :' + $scope.cliente.nombre);
            $rootScope.$broadcast('clearAlerts');
          }else{
            $rootScope.$broadcast('error', "No existe cliente con la identificacion ingresada");
          }
        },
        function(error){
           $rootScope.$broadcast('error');
        });
    }
  };
  
  
  // Clears the form. Either by clicking the 'Clear' button in the form, or when a successfull save is performed.
  $scope.clearForm = function () {
    $scope.cliente = null;
    $scope.nuevaCuenta=null;
    // Resets the form validation state.
    $scope.cuentaForm.$setPristine();
    // Broadcast the event to also clear the grid selection.
    $rootScope.$broadcast('clear');
  };

  // Calls the rest method to save a Cliente.
  $scope.createCuenta = function () {
    cuentaService.save($scope.nuevaCuenta,
       function (result) {
        $scope.cuenta = result;        
        console.log("Resultado crear cuenta:" +  $scope.cuenta.numCuenta);
        $rootScope.$broadcast('cuentaCreada');
       },
       function (error) {
         // Broadcast the event for a server error.
         console.log('Mensaje error:'+ error.data.message);
         console.log('Codigo error :' + error.data.code);
         console.log('Status:'+ error.status );
      
         $rootScope.$broadcast('error', "Ops!, algo pasa ... " + error.data.message);
       }
       );
  };

  // Picks up the event broadcasted when the person is selected from the grid and perform the person load by calling
  // the appropiate rest service.
  $scope.$on('clienteSelected', function (event, id) {
    console.log('Cliente seleccionado, ID = '+ id);
    $scope.cliente = clienteService.get({id: id});
  });
  
  $scope.$on('cuentaCreada', function(){
    var modalOptions = {
          //closeButtonText: 'Cerrar',
          actionButtonText: 'Continuar',
          headerText: 'Resultado de operación',
          bodyText: 'Se ha creado la cuenta No.' + $scope.cuenta.numCuenta +
              ". Con saldo inicial $" + $scope.cuenta.saldo
      };

      modalService.showModal({}, modalOptions,true).then(function () {
        $scope.clearForm();
        $state.go('home');
      });
  });
  
  $scope.cancelar = function(){
    $state.go('home');
  };
  
});
