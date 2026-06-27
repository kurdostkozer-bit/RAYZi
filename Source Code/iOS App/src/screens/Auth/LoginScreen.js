import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
  TouchableOpacity,
  StatusBar,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {useDispatch, useSelector} from 'react-redux';
import Icon from 'react-native-vector-icons/Ionicons';

import {Button, Input, Loading} from '../../components';
// تصحيح مسار استدعاء الخدمة المباشر لمنع أخطاء الـ undefined
import authService from '../../services/authService';
import {loginStart, loginSuccess, loginFailure} from '../../store/slices/authSlice';
import {saveToken} from '../../services/api';
import socketService from '../../services/socketService';

const LoginScreen = ({navigation}) => {
  const dispatch = useDispatch();
  const {loading} = useSelector(state => state.auth);

  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);

  const validateForm = () => {
    const newErrors = {};

    if (!formData.email) {
      newErrors.email = 'البريد الإلكتروني مطلوب';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'البريد الإلكتروني غير صالح';
    }

    if (!formData.password) {
      newErrors.password = 'كلمة المرور مطلوبة';
    } else if (formData.password.length < 6) {
      newErrors.password = 'كلمة المرور يجب أن تكون 6 أحرف على الأقل';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleLogin = async () => {
    if (!validateForm()) return;

    dispatch(loginStart());

    try {
      const response = await authService.login(formData.email, formData.password);
      
      // حفظ التوكن بنجاح في ذاكرة الجهاز
      await saveToken(response.token);
      
      // تشغيل السوكيت الحي فور الدخول لبدء استلام أحداث اللعب والتنبيهات
      try {
        await socketService.connect();
      } catch (socketError) {
        console.warn('📡 Live socket connection deferred:', socketError.message);
      }

      // تحديث حالة Redux (والتي ستتولى نقله للرئيسية تلقائياً وبأمان)
      dispatch(loginSuccess({
        token: response.token,
        user: response.user,
      }));

    } catch (error) {
      // تمرير نص الخطأ القادم من السيرفر مباشرة للواجهة
      dispatch(loginFailure(error?.message || 'فشل تسجيل الدخول، تحقق من البيانات'));
    }
  };

  const handleSocialLogin = async (provider) => {
    console.log(`${provider} login clicked`);
  };

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.container}>
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={styles.keyboardContainer}>
          <ScrollView
            contentContainerStyle={styles.scrollContent}
            showsVerticalScrollIndicator={false}>
            <View style={styles.header}>
              <Text style={styles.title}>مرحباً بعودتك</Text>
              <Text style={styles.subtitle}>سجل دخولك للمتابعة</Text>
            </View>

            <View style={styles.form}>
              <Input
                label="البريد الإلكتروني"
                placeholder="أدخل بريدك الإلكتروني"
                value={formData.email}
                onChangeText={text => setFormData({...formData, email: text})}
                keyboardType="email-address"
                autoCapitalize="none"
                icon="mail-outline"
                error={errors.email}
              />

              <Input
                label="كلمة المرور"
                placeholder="أدخل كلمة المرور"
                value={formData.password}
                onChangeText={text => setFormData({...formData, password: text})}
                secureTextEntry={!showPassword}
                icon="lock-closed-outline"
                error={errors.password}
              />

              <TouchableOpacity
                style={styles.forgotPassword}
                onPress={() => navigation.navigate('ForgotPassword')}>
                <Text style={styles.forgotPasswordText}>نسيت كلمة المرور؟</Text>
              </TouchableOpacity>

              <Button
                title="تسجيل الدخول"
                onPress={handleLogin}
                loading={loading}
                style={styles.loginButton}
              />

              <View style={styles.divider}>
                <View style={styles.dividerLine} />
                <Text style={styles.dividerText}>أو</Text>
                <View style={styles.dividerLine} />
              </View>

              <View style={styles.socialButtons}>
                <TouchableOpacity
                  style={styles.socialButton}
                  onPress={() => handleSocialLogin('google')}>
                  <Icon name="logo-google" size={24} color="#DB4437" />
                </TouchableOpacity>
                <TouchableOpacity
                  style={styles.socialButton}
                  onPress={() => handleSocialLogin('facebook')}>
                  <Icon name="logo-facebook" size={24} color="#4267B2" />
                </TouchableOpacity>
                <TouchableOpacity
                  style={styles.socialButton}
                  onPress={() => handleSocialLogin('apple')}>
                  <Icon name="logo-apple" size={24} color="#000" />
                </TouchableOpacity>
              </View>

              <View style={styles.registerContainer}>
                <Text style={styles.registerText}>ليس لديك حساب؟</Text>
                <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                  <Text style={styles.registerLink}>إنشاء حساب</Text>
                </TouchableOpacity>
              </View>
            </View>
          </ScrollView>
        </KeyboardAvoidingView>
      </LinearGradient>
      <Loading visible={loading} text="جاري تسجيل الدخول..." />
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  keyboardContainer: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    padding: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 40,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 10,
  },
  subtitle: {
    fontSize: 16,
    color: 'rgba(255, 255, 255, 0.9)',
  },
  form: {
    backgroundColor: '#fff',
    borderRadius: 20,
    padding: 25,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 5,
  },
  forgotPassword: {
    alignSelf: 'flex-end',
    marginBottom: 20,
  },
  forgotPasswordText: {
    fontSize: 14,
    color: '#e60a57',
    fontWeight: '600',
  },
  loginButton: {
    marginBottom: 20,
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 20,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: '#eee',
  },
  dividerText: {
    marginHorizontal: 15,
    fontSize: 14,
    color: '#888',
  },
  socialButtons: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 20,
  },
  socialButton: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: '#f5f5f5',
    justifyContent: 'center',
    alignItems: 'center',
    marginHorizontal: 10,
  },
  registerContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  registerText: {
    fontSize: 14,
    color: '#666',
  },
  registerLink: {
    fontSize: 14,
    color: '#e60a57',
    fontWeight: 'bold',
    marginLeft: 5,
  },
});

export default LoginScreen;
