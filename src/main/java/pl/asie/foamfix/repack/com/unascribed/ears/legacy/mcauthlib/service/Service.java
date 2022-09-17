/**
 * MCAuthLib
 * 
 * Copyright (C) 2013-2021 Steveice10
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pl.asie.foamfix.repack.com.unascribed.ears.legacy.mcauthlib.service;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Base class for auth-related services.
 */
public abstract class Service {
	private URI baseUri;
	private Proxy proxy = Proxy.NO_PROXY;

	public Service(URI initialBaseUri) {
		if(initialBaseUri == null) {
			throw new IllegalArgumentException("Initial Base URI cannot be null.");
		}

		this.baseUri = initialBaseUri;
	}

	/**
	 * Gets the base URI of this service.
	 * Endpoint names will be appended to the base URI when making requests.
	 *
	 * @return The service's base URI.
	 */
	public URI getBaseUri() {
		return this.baseUri;
	}

	/**
	 * Sets the base URI of this service.
	 * Endpoint names will be appended to the base URI when making requests.
	 *
	 * @param baseUri The base URI to use.
	 * @throws IllegalArgumentException If the provided base URI is null or malformed.
	 */
	public void setBaseUri(String baseUri) {
		if(baseUri == null) {
			throw new IllegalArgumentException("Base URI cannot be null.");
		}

		try {
			this.setBaseUri(new URI(baseUri));
		} catch(URISyntaxException e) {
			throw new IllegalArgumentException("Invalid base URI.", e);
		}
	}

	/**
	 * Sets the base URI of this service.
	 * Endpoint names will be appended to the base URI when making requests.
	 *
	 * @param baseUri The base URI to use.
	 * @throws IllegalArgumentException If the provided base URI is null.
	 */
	public void setBaseUri(URI baseUri) {
		if(baseUri == null) {
			throw new IllegalArgumentException("Base URI cannot be null.");
		}

		this.baseUri = baseUri;
	}

	/**
	 * Gets the URI of a specific endpoint of this service.
	 *
	 * @param endpoint Endpoint to get the URI of.
	 * @return The URI for the given endpoint.
	 */
	public URI getEndpointUri(String endpoint) {
		return this.baseUri.resolve(endpoint);
	}

	/**
	 * Gets the URI of a specific endpoint of this service.
	 *
	 * @param endpoint Endpoint to get the URI of.
	 * @param queryParams Query parameters to append to the URI.
	 * @return The URI for the given endpoint.
	 */
	public URI getEndpointUri(String endpoint, Map<String, String> queryParams) {
		URI base = this.getEndpointUri(endpoint);
		try {
			StringBuilder queryString = new StringBuilder();
			for(Map.Entry<String, String> queryParam : queryParams.entrySet()) {
				if(queryString.length() > 0) {
					queryString.append("&");
				}

				queryString.append(queryParam.getKey())
				.append('=')
				.append(URLEncoder.encode(queryParam.getValue(), "UTF-8"));
			}

			return new URI(base.getScheme(), base.getAuthority(), base.getPath(), queryString.toString(), base.getFragment());
		} catch(URISyntaxException e) {
			throw new IllegalArgumentException("Arguments resulted in invalid endpoint URI.", e);
		} catch(UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not supported.", e);
		}
	}

	/**
	 * Gets the proxy used by this service.
	 *
	 * @return THe proxy used by this service.
	 */
	public Proxy getProxy() {
		return this.proxy;
	}

	/**
	 * Sets the proxy used by this service.
	 *
	 * @param proxy Proxy to use. Null will be converted to NO_PROXY.
	 */
	public void setProxy(Proxy proxy) {
		if(proxy == null) {
			this.proxy = Proxy.NO_PROXY;
		} else {
			this.proxy = proxy;
		}
	}
}
