import 'react-native-gesture-handler';
import { AppRegistry } from 'react-native';
import App from './App';
import { name as appName } from './app.json';

// تسجيل المكون الرئيسي للعبة ليعمل على نظام iOS بنجاح
AppRegistry.registerComponent(appName, () => App);
