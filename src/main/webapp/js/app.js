angular.module('todoApp', [])
  .controller('TodoController', ['$scope', function TodoController($scope) {
    $scope.sayHi = function() {
      console.log("sayHi")
      var text = $scope.text
      $scope.text = ""
      angularBackend.sayHi(text).then(function(data) {
        $scope.$apply(function() {
          // $scope.text = ""
        })
      })
    }

    $scope.sendMessage = function() {
      console.log("sendMessage...")
      var msg = $scope.msg
      $scope.msg = ""
      sendMessage(msg)
    }
  }])