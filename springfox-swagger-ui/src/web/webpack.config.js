const MinifyPlugin = require("babel-minify-webpack-plugin");

module.exports = {
  entry: ["babel-polyfill", "./js/springfox.js"],
  output: {
    path: __dirname + "/dist",
    filename: "springfox.js",
  },
  plugins: [
    new MinifyPlugin()
  ],
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
        }
      }
    ]
  }
};