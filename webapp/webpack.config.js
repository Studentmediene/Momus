const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const { BaseHrefWebpackPlugin } = require('base-href-webpack-plugin');
const webpack = require('webpack');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const path = require('path');

const devServerPort = process.env.DEV_SERVER_PORT || 8082;
const proxyApiHost = process.env.PROXY_API_HOST || 'localhost';
const proxyApiPort = process.env.PROXY_API_PORT || '8080';
const proxyApi = `${proxyApiHost}:${proxyApiPort}`;

const root = __dirname;
const dev = root;
const out = path.join(root, 'dist');
const publicPath = '/';
const assets = 'assets/'; // Folder for all js/css and other assets
const partials = 'partials/'; // HTML templates

const isDevServer = process.argv.some(v => v.includes('webpack-dev-server'));
const isprod = process.argv.indexOf('-p') !== -1;

const commonPlugins = [
    // Injects bundles into the index file
    new HtmlWebpackPlugin({
        template: path.join(dev, 'index.html'),
        favicon: path.join(dev, 'favicon.ico'),
        filename: path.join(out, isDevServer ? 'index.html' : path.join(assets, 'index.html')),
    }),
    new webpack.ProvidePlugin({
        $: "jquery",
        jQuery: "jquery",
        "window.jQuery": "jquery"
    }),
    new CopyWebpackPlugin([
        { from: path.join(dev, partials), to: path.join(path.join(out, assets), partials) },
    ]),
    new BaseHrefWebpackPlugin({ baseHref: '/' })
];

const prodPlugins = [
    new CleanWebpackPlugin(
        [out],
        {exclude: '.gitkeep'}
    ),
    new MiniCssExtractPlugin({
        filename: path.join(assets, '/[name]-[contenthash].css')
    }),
    new UglifyJsPlugin({
        uglifyOptions: {
            mangle: true,
            beautify: true
        }
    })
];

const plugins = commonPlugins.concat(isprod ? prodPlugins : []);

const devServer = {
    publicPath: publicPath,
    host: '0.0.0.0',
    port: devServerPort,
    historyApiFallback: true,
    proxy: {
        '/api': `http://${proxyApi}`,
        '/saml': `http://${proxyApi}`,
        '/api/ws': {
            target: `ws://${proxyApi}`,
            ws: true
        }
    }
};

module.exports = {
    entry: {
        main: path.join(dev, 'js/app.js')
    },
    output: {
        path: out,
        publicPath: publicPath,
        filename: path.join(assets, '[name]-[chunkhash].js'),
        chunkFilename: path.join(assets, '[name]-[chunkhash].js'),
    },
    devtool: isprod ? false : 'inline-source-map',
    module: {
        rules: [
            {
                test: /\.css$/,
                include: /node_modules/,
                use: [
                    { loader: isprod ? MiniCssExtractPlugin.loader : 'style-loader' },
                    'css-loader']
            }, {
                test: /\.scss$/,
                exclude: /node_modules/,
                use: [
                    { loader: isprod ? MiniCssExtractPlugin.loader : 'style-loader' },
                    'css-loader',
                    'sass-loader']
            }, {
                test: /\.html$/,
                include: dev,
                loader: 'html-loader'
            }, {
                test: /\.js$/,
                include: dev,
                use: [
                    'ng-annotate-loader?ngAnnotate=ng-annotate-patched', 
                    {
                        loader:'babel-loader',
                        options: {
                            presets: ['@babel/preset-env'],
                            plugins: [require('@babel/plugin-proposal-object-rest-spread')],
                        }
                    }
                ]
            }, {
                test: /\.(png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot)$/,
                use: [{ loader: 'file-loader', options: {outputPath: assets }}]
            }, {
                test: /ui-sortable/,
                use: ['imports-loader?$UI=jquery-ui/ui/widgets/sortable']
            }
        ]
    },
    resolve: {
        extensions: [ '.js', '.html' ],
        modules: [
            dev,
            "node_modules"
        ]
    },
    plugins: plugins,
    devServer: devServer,
    mode: isprod ? 'production' : 'development',
    node: {
        fs: 'empty',
        net: 'empty',
        tls: 'empty'
    },
    optimization: {
        runtimeChunk: 'single',
        splitChunks: {
            cacheGroups: {
                vendors: {
                    test: /[\\/]node_modules[\\/]/,
                    name: 'vendors',
                    enforce: true,
                    chunks: 'all'
                }
            }
        }
    }
};
