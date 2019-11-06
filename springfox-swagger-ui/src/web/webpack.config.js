const path = require('path');

module.exports = {
  entry: ['babel-polyfill', './js/springfox.js'],
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'springfox.js',
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
        }
      }
    ]
  },
  devtool: 'source-map'
};