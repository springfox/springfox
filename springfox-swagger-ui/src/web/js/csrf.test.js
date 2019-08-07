import 'babel-polyfill';
import patchRequestInterceptor, {getCsrf} from './csrf';
import fetchMock from 'fetch-mock';
import { FetchError } from 'node-fetch';

const baseUrl = 'http://mock.com';
const headerName = 'X-XSRF-TOKEN';
const cookieName = 'XSRF-TOKEN';
const token = 'b11d3ee4-51d4-4eda-9980-6c07e527eb44';

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

async function expectOk() {
  const response = await getCsrf(baseUrl);
  expect(response.token).toBe(token);
  expect(response.headerName).toBe(headerName);
}

/*
 * 1 2 3: get from meta, get from endpoint, get from cookie
 * x: mock csrf not found
 * o: mock found!
 * ?: mock not define
 */

test('x x x', async () => {
  fetchMock.mock(`${baseUrl}/`, 404);
  fetchMock.mock(`${baseUrl}/csrf`, 404);

  const response = await getCsrf(baseUrl);
  expect(response).toBe(undefined);
});

test('o ? ?', async () => {
  fetchMock.mock(`${baseUrl}/`, `<html><head><title>Title</title>
  <meta name="_csrf" content="${token}">
  <meta name="_csrf_header" content="${headerName}">
</head><body></body></html>`);

  await expectOk();
});

test('x o ?', async () => {
  fetchMock.mock(`${baseUrl}/`, `<html><head><title>No Meta</title></head><body></body></html>`);
  fetchMock.mock(`${baseUrl}/csrf`, { headerName, token });

  await expectOk();
});

test('x x o', async () => {
  fetchMock.mock(`${baseUrl}/`, 404);
  fetchMock.mock(`${baseUrl}/csrf`, 404);
  document.cookie = 'first=hi';
  document.cookie = `${cookieName}=${token}`;
  document.cookie = 'last=hi';
  await expectOk();
});

test('x wrong-json ?', async () => {
  fetchMock.mock(`${baseUrl}/`, 404);
  fetchMock.mock(`${baseUrl}/csrf`, {
    nothing: 'nothing'
  });

  const response = await getCsrf(baseUrl);
  expect(response).toBe(undefined);
});

test('x invalid-json ?', async () => {
  fetchMock.mock(`${baseUrl}/`, 404);
  fetchMock.mock(`${baseUrl}/csrf`, 'bla bla bla');

  let error;
  try {
    await getCsrf(baseUrl)
  } catch (e) {
    error = e;
  }
  expect(error).toBeInstanceOf(FetchError);

  // Make sure this function will not throw exception.
  await patchRequestInterceptor(baseUrl);
});
