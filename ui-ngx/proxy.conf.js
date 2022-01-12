/*
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const forwardUrl = "http://iot.textile-saas.huansi.net"; // http://iot.textile-saas.huansi.net  http://10.10.10.162:8080  http://10.10.11.23:8080  http://10.10.10.179:8080  http://10.10.10.55:8080
const wsForwardUrl = "ws://iot.textile-saas.huansi.net";
const ruleNodeUiforwardUrl = forwardUrl;

const PROXY_CONFIG = {
  "/api": {
    "target": forwardUrl,
    "secure": false,
    "changeOrigin": true
  },
  "/static/rulenode": {
    "target": ruleNodeUiforwardUrl,
    "secure": false,
    "changeOrigin": true
  },
  "/static/widgets": {
    "target": forwardUrl,
    "secure": false,
    "changeOrigin": true
  },
  "/oauth2": {
    "target": forwardUrl,
    "secure": false,
    "changeOrigin": true
  },
  "/login/oauth2": {
    "target": forwardUrl,
    "secure": false,
    "changeOrigin": true
  },
  "/api/ws": {
    "target": wsForwardUrl,
    "ws": true,
    "secure": false,
    "changeOrigin": true
  },
};

module.exports = PROXY_CONFIG;
