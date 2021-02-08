/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/

'use strict';

/* App Module */

var leshanApp = angular.module('leshanApp',[ 
        'ngRoute',
        'clientControllers',
        'objectDirectives',
        'instanceDirectives',
        'resourceDirectives',
        'resourceFormDirectives',
        'lwResourcesServices',
        'securityControllers',
        'uiDialogServices',
        'modalInstanceControllers',
        'modalResourceControllers',
        'ui.bootstrap',
        'helperServices',
        'fileModelDirectives'
]);

leshanApp.value('uriBase', 'lwm2m/api');

leshanApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.
        when('/clients',           { templateUrl : 'partials/client-list.html',   controller : 'ClientListCtrl' }).
        when('/clients/:clientId', { templateUrl : 'partials/client-detail.html', controller : 'ClientDetailCtrl' }).
        when('/security',          { templateUrl : 'partials/security-list.html', controller : 'SecurityCtrl' }).
        otherwise({ redirectTo : '/clients' });
}]);

leshanApp.run(function ($rootScope) {
  $rootScope.addToken = function (token) {
    localStorage.setItem('token', token);
  };
});

leshanApp.config([
  "$httpProvider",
  function ($httpProvider) {
    $httpProvider.interceptors.push("AuthInterceptor");
  },
]);

leshanApp.factory("AuthInterceptor", [
  "$q",
  function ($q) {
    return {
      // Add an interceptor for requests.
      request: function (config) {
        config.headers = config.headers || {}; // Default to an empty object if no headers are set.

        // Set the header if the token is stored.
        if (localStorage.getItem('token')) {
          config.headers["X-Authorization"] = 'Bearer ' + localStorage.getItem('token');
        }
        return config;
      },

      // Add an interceptor for any responses that error.
      responseError: function (response) {
        // Check if the error is auth-related.
        //       if (response.status === 401 || response.status === 403) {
        //         $state.go("login");
        //       }
        return $q.reject(response);
      },
    };
  },
]);

