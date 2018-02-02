module.exports = {
  entry: ["babel-polyfill", "./js/springfox.js"],
  output: {
    path: __dirname + "/dist",
    filename: "springfox.js",
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /(node_modules|bower_components)/,
        use: {
          loader: "babel-loader",
        }
      }
    ]
  }
}