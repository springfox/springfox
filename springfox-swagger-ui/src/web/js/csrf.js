export const TokenStore = {
  NONE: "NONE",
  SESSION: "SESSION",
  COOKIE: "COOKIE"
}

/**
 * Add csrf header if necessary.
 * @param baseUrl
 * @returns {Promise<void>}
 */
export default async function patchRequestInterceptor(baseUrl, strategy) {
  try {
    if (!strategy || strategy.tokenStore == TokenStore.NONE) {
      return;
    }
    const result = await getCsrf(baseUrl, strategy);

    if (result) {
      window.ui.getConfigs().requestInterceptor = request => {
        request.headers[result.headerName] = result.token;
        // console.debug(request);
        return request;
      };
      console.debug('Successfully registered interceptor that adds `csrf header` to any request');
    } else {
      console.debug('No csrf token has been found');
    }
  } catch (e) {
    console.error('Failed on registering interceptor', e)
  }
}

/**
 * Get csrf information. When the token is stored in session (TokenStore.SESSION)
 * this method will fetch the csrf token from the `/swagger-resources/csrf` endpoint,
 * and when stored in cookie, it will try to get it from cookie, and then try the 
 * endpoint if failed.
 * @param baseUrl The base url
 * @returns {Promise<{headerName: string, token: string} | undefined>}
 */
export async function getCsrf(baseUrl, strategy) {
  if (!strategy || strategy.tokenStore == TokenStore.NONE) {
    return null;
  }
  let endpoint = `${baseUrl}/swagger-resources/csrf`;
  switch(strategy.tokenStore) {
    case TokenStore.SESSION:
      return await getCsrfFromEndpoint(endpoint)
      .then(v => v || null);
    case TokenStore.COOKIE:
      return await getCsrfFromCookie(strategy.keyName, strategy.headerName)
      .then(v => v || getCsrfFromEndpoint(endpoint));
    default:
      return null;  
  }
}

/**
 * Get from an endpoint which gives the right csrf token we need.
 * @param url The endpoint
 * @returns {Promise<{headerName: string, token: string}> | undefined}
 */
async function getCsrfFromEndpoint(url) {
  const jsonResponse = await fetch(url, {credentials: 'same-origin'});
  if (jsonResponse.status !== 200) return;

  const json = await jsonResponse.json();
  if (json.headerName && json.token) {
    return json;
  }
}

/**
 * Get from cookie locally.
 * @param name The name of the cookie which stores the csrf token we need
 * @param headerName The name of http header to be returned
 * @returns {{headerName: string, token: string} | undefined}
 */
async function getCsrfFromCookie(name, headerName) {
  const matcher = document.cookie.match(`(^|;)\\s*${name}\\s*=\\s*([^;]+)`);
  if (matcher) {
    return {
      headerName: headerName,
      token: matcher.pop()
    }
  }
}
