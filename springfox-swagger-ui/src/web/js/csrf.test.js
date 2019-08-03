import 'babel-polyfill';
import patchRequestInterceptor, {TokenStore, getCsrf} from './csrf';
import fetchMock from 'fetch-mock';
import { FetchError } from 'node-fetch';

const baseUrl = 'http://mock.com';
const token = 'b11d3ee4-51d4-4eda-9980-6c07e527eb44';
const strategy = {
  tokenStore: TokenStore.COOKIE,
  parameterName: "_csrf",
  headerName: "X-XSRF-TOKEN",
  keyName: "XSRF-TOKEN"
};
const endpoint = `${baseUrl}/swagger-resources/csrf`;

afterEach(() => {
  fetchMock.reset();
  fetchMock.restore();
  // clear cookie
  document.cookie
      .split(";")
      .forEach((c) => {
        document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
      });
});

async function expectOk(strategy) {
  const response = await getCsrf(baseUrl, strategy);
  expect(response.token).toBe(token);
  expect(response.headerName).toBe(strategy.headerName);
}

/**====================*
 * patching ability    *
 *====================**/

test('the patcher function should catch no errors', async () => {
  // Make sure this function will not throw any exception.
  await patchRequestInterceptor(baseUrl, strategy);
});

test('no strategy specified', async () => {
  const config = {requestInterceptor: (a => a)}
  const configGetter = jest.fn().mockImplementation(() => {return config});
  window.ui = {
    getConfigs: configGetter
  }

  await patchRequestInterceptor(baseUrl);
  expect(configGetter).not.toBeCalled();
});

test('no token could be acquired', async () => {
  fetchMock.mock(endpoint, `{"token":null,"parameterName":"${strategy.parameterName}","headerName":"${strategy.headerName}"}`);
  strategy.tokenStore = TokenStore.SESSION;

  await patchRequestInterceptor(baseUrl, strategy);
});

test('when everything is properly set, the patcher and patched function should both work', async () => {
  // mock
  const config = {requestInterceptor: (a => a)}
  const configGetter = jest.fn().mockImplementation(() => {return config});
  window.ui = {
    getConfigs: configGetter
  }
  fetchMock.mock(endpoint, `{"token":"${token}","parameterName":"${strategy.parameterName}","headerName":"${strategy.headerName}"}`);
  strategy.tokenStore = TokenStore.SESSION;
  
  // patcher function
  await patchRequestInterceptor(baseUrl, strategy);
  expect(configGetter).toBeCalledTimes(1);

  // patched function
  let request = window.ui.getConfigs().requestInterceptor.call(this, {headers: {}});
  expect(request.headers[strategy.headerName]).toBe(token);
});

/**===============*
 * normal tests   *
 *===============**/

test('csrf token not supported, but the user still configured the strategy to be cookie-store typed', async () => {
  fetchMock.mock(endpoint, `{"token":null,"parameterName":"${strategy.parameterName}","headerName":"${strategy.headerName}"}`);
  strategy.tokenStore = TokenStore.COOKIE;

  const response = await getCsrf(baseUrl, strategy);
  expect(response).toBeUndefined();
});

test('cookie-store typed csrf token is locked-n-loaded', async () => {
  document.cookie = 'first=hi';
  document.cookie = `${strategy.keyName}=${token}`;
  document.cookie = 'last=hi';
  strategy.tokenStore = TokenStore.COOKIE;

  await expectOk(strategy);
});

test('session-store typed csrf token is locked-n-loaded', async () => {
  fetchMock.mock(endpoint, `{"token":"${token}","parameterName":"${strategy.parameterName}","headerName":"${strategy.headerName}"}`);
  strategy.tokenStore = TokenStore.SESSION;

  await expectOk(strategy);
});

test('token-store specified incorrectly', async () => {
  fetchMock.mock(endpoint, `{"token":"${token}","parameterName":"${strategy.parameterName}","headerName":"${strategy.headerName}"}`);
  strategy.tokenStore = "Moo~";

  const response = await getCsrf(baseUrl, strategy);
  expect(response).toBeNull();
});

test('token-store specified as NONE', async () => {
  fetchMock.mock(endpoint, `{"token":"${token}","parameterName":"${strategy.parameterName}","headerName":"${strategy.headerName}"}`);
  strategy.tokenStore = TokenStore.NONE;

  const response = await getCsrf(baseUrl, strategy);
  expect(response).toBeNull();
});

/**==========================*
 * malformed response tests  *
 *==========================**/

test('response status code is not 200', async () => {
  fetchMock.mock(endpoint, 403);
  strategy.tokenStore = TokenStore.COOKIE; // this can cover more methods

  const response = await getCsrf(baseUrl, strategy);
  expect(response).toBeUndefined();
});

test('malformed json response', async () => {
  fetchMock.mock(endpoint, "bla bla bla");

  let error;
  try {
    await getCsrf(baseUrl, strategy)
  } catch (e) {
    error = e;
  }
  expect(error).toBeInstanceOf(FetchError);
});
