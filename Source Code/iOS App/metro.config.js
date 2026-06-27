const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');

/**
 * Metro configuration
 * https://facebook.github.io/metro/docs/configuration
 *
 * @type {import('metro-config').MetroConfig}
 */
module.exports = (async () => {
  const defaultConfig = await getDefaultConfig(__dirname);
  
  const config = {
    watchFolders: [],
    resolver: {
      // إدخال امتدادات الأكواد والـ svg كملفات برمجية
      sourceExts: ['js', 'jsx', 'ts', 'tsx', 'json', 'svg'],
      // إبقاء الصور والخطوط العادية فقط وحذف الـ svg منها لمنع التعارض
      assetExts: defaultConfig.resolver.assetExts.filter(ext => ext !== 'svg'),
    },
    transformer: {
      getTransformOptions: async () => ({
        transform: {
          experimentalImportSupport: false,
          inlineRequires: true,
        },
      }),
      // تشغيل محول الرسوميات لربط أيقونات المراهنات والألعاب
      babelTransformerPath: require.resolve('react-native-svg-transformer'),
    },
  };

  return mergeConfig(defaultConfig, config);
})();
