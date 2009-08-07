/*
 * SocialSite extends Shindig's normal behavior so that this file is processed
 * to allow property expansion (with properties defined via
 * com.sun.socialsite.config.Config)
 */

{ "gadgets.container" : ["socialsite"],
  "gadgets.features" : {
    "rpc" : {
      // Path to the relay file. Automatically appended to the parent
      // parameter if it passes input validation and is not null.
      // This should never be on the same host in a production environment!
      // Only use this for TESTING!
      "parentRelayUrl" : "${socialsite.base.url}/gadgets/files/container/rpc_relay.html",
    },
    "opensocial-0.8" : {
      // Path to fetch opensocial data from
      // Must be on the same domain as the gadget rendering server
      "path" : "http://%host%${context.contextpath}/social",
    },
    "core.io" : {
      // Note: /proxy is an open proxy. Be careful how you expose this!
      "proxyUrl" : "http://%host%${context.contextpath}/gadgets/proxy?refresh=%refresh%&url=%url%",
      "jsonProxyUrl" : "http://%host%${context.contextpath}/gadgets/makeRequest"
    },
  }
}
