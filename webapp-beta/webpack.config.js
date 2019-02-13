const HtmlWebpackPlugin = require('html-webpack-plugin');
const { BaseHrefWebpackPlugin } = require('base-href-webpack-plugin');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const Visualizer = require('webpack-visualizer-plugin');
const path = require('path');

const devServerPort = process.env.DEV_SERVER_PORT || 8081;
const proxyApiHost = process.env.PROXY_API_HOST || 'localhost';
const proxyApiPort = process.env.PROXY_API_PORT || '8080';
const proxyApi = `${proxyApiHost}:${proxyApiPort}`;

const root = __dirname;
const dev = root;
const out = path.join(root, 'dist');
const publicPath = '/';
const assets = 'assets/';

const isprod = process.argv.indexOf('-p') !== -1;
const visualize = process.argv.indexOf('--visualizer') !== -1;
const isbeta = process.argv.indexOf('--beta') !== -1;

const indexPath = path.join(publicPath, isbeta ? '/beta/' : '/');

const commonPlugins = [
    // Injects bundles into the index file
    new HtmlWebpackPlugin({
        template: path.join(dev, 'index.html'),
        favicon: path.join(dev, 'favicon.ico'),
        filename: path.join(path.join(out, isbeta ? '/assets/beta/' : '/'), 'index.html'),
    }),
    new BaseHrefWebpackPlugin({ baseHref: indexPath })
];

const prodPlugins = [
    new CleanWebpackPlugin(
        [out],
        {exclude: '.gitkeep'}
    ),
    new MiniCssExtractPlugin({
        filename: path.join(assets, `${isbeta ? 'beta-' : ''}[name]-[contenthash].css`),
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
    host: '0.0.0.0',
    publicPath: publicPath,
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
        main: path.join(dev, 'app.ts')
    },
    output: {
        path: out,
        publicPath: publicPath,
        filename: path.join(assets, `${isbeta ? 'beta-' : ''}[name]-[chunkhash].js`),
        chunkFilename: path.join(assets, `${isbeta ? 'beta-' : ''}[name]-[chunkhash].js`),
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
                test: /\.ts$/,
                include: dev,
                use: ['ng-annotate-loader?ngAnnotate=ng-annotate-patched','ts-loader']
            }, {
                test: /\.(png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot)$/,
                use: [{ loader: 'file-loader', options: {outputPath: assets, publicPath: path.join('/', assets)} }]
            }, {
                test: /ui-sortable/,
                use: ['imports-loader?$UI=jquery-ui/ui/widgets/sortable']
            }
        ]
    },
    resolve: {
        extensions: [ '.ts', '.js', '.html' ],
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
