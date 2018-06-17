/**
 * Add csrf header if necessary.
 * @param baseUrl
 * @returns {Promise<void>}
 */
export default async function patchRequestInterceptor(baseUrl) {
  try {
    const result = await getCsrf(baseUrl);

    if (result) {
      window.ui.getConfigs().requestInterceptor = request => {
        request.headers[result.headerName] = result.token;
        // console.debug(request);
        return request;
      };
      console.debug('Successfully added csrf header for all requests');
    } else {
      console.debug('No csrf token can be found');
    }
  } catch (e) {
    console.error('Add csrf header encounter error', e)
  }
}

/**
 * 1. getCsrfFromMeta.
 * 2. getCsrfFromEndpoint.
 * 3. getCsrfFromCookie
 * @param baseUrl
 * @returns {Promise<{headerName: string, token: string} | undefined>}
 */
export async function getCsrf(baseUrl) {
  return await getCsrfFromMeta(baseUrl)
  .then(v => v ? v : getCsrfFromEndpoint(baseUrl))
  .then(v => v ? v : getCsrfFromCookie());
}

/**
 * 1. get from meta, default endpoint is '/'.
 * @param baseUrl
 * @returns {Promise<{headerName: string, token: string}> | undefined}
 */
async function getCsrfFromMeta(baseUrl) {
  const htmlResponse = await fetch(`${baseUrl}/`, {credentials: 'same-origin'});
  if (htmlResponse.status !== 200) return;

  const html = await htmlResponse.text();
  const dummy = document.createElement('div');
  dummy.innerHTML = html;
  const headerDom = dummy.querySelector('meta[name="_csrf_header"]');
  const csrfDom = dummy.querySelector('meta[name="_csrf"]');
  if (headerDom !== null && csrfDom !== null ) {
    const headerName = headerDom.getAttribute('content');
    const token = csrfDom.getAttribute('content');
    if (headerName !== null && token !== null) {
      return { headerName, token }
    }
  }
}

/**
 * 2. get from '/csrf' endpoint.
 * @param baseUrl
 * @returns {Promise<{headerName: string, token: string}> | undefined}
 */
async function getCsrfFromEndpoint(baseUrl) {
  const jsonResponse = await fetch(`${baseUrl}/csrf`, {credentials: 'same-origin'});
  if (jsonResponse.status !== 200) return;

  const json = await jsonResponse.json();
  if (json.headerName && json.token) {
    return json;
  }
}

/**
 * 3. get from cookie.
 * @returns {{headerName: string, token: string} | undefined}
 */
function getCsrfFromCookie() {
  const name = 'XSRF-TOKEN';
  const matcher = document.cookie.match(`(^|;)\\s*${name}\\s*=\\s*([^;]+)`);
  if (matcher) {
    return {
      headerName: 'X-XSRF-TOKEN',
      token: matcher.pop()
    }
  }
}
