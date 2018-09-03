const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const webpack = require('webpack');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const Visualizer = require('webpack-visualizer-plugin');
const path = require('path');

const root = __dirname;
const dev = root;
const out = path.join(root, 'dist');

const isprod = process.argv.indexOf('-p') !== -1;
const visualize = process.argv.indexOf('--visualizer') !== -1;

const assetsPath = path.join(out, 'assets/');

const commonPlugins = [
    // Injects bundles into the index file
    new HtmlWebpackPlugin({
        template: path.join(dev, 'index.html'),
        favicon: path.join(dev, 'favicon.ico'),
        filename: path.join(out, 'index.html'),
    }),
    new webpack.ProvidePlugin({
        $: "jquery",
        jQuery: "jquery",
        "window.jQuery": "jquery"
    }),
    new CopyWebpackPlugin([
        { from: path.join(dev, "partials"), to: path.join(out, "partials") },
    ]),
];

const prodPlugins = [
    new CleanWebpackPlugin([
        out
    ]),
    new MiniCssExtractPlugin({
        filename: '[name]-[contenthash].css'
    }),
    new UglifyJsPlugin({
        uglifyOptions: {
            mangle: true,
            beautify: true
        }
    })
];

const plugins = commonPlugins.concat(isprod ? prodPlugins : []);
if (visualize) plugins.push(new Visualizer());

const devServer = {
    contentBase: out,
    port: 8081,
    historyApiFallback: true,
    proxy: {
        '/api': 'http://localhost:8080',
        '/saml': 'http://localhost:8080',
        '/api/ws': {
            target: 'ws://localhost:8080',
            ws: true
        }
    }
};

module.exports = {
    entry: {
        main: path.join(dev, 'js/app.js')
    },
    output: {
        path: assetsPath,
        filename: '[name]-[chunkhash].js',
        chunkFilename: '[name]-[chunkhash].js'
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
                use: [{ loader: 'file-loader' }]
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
