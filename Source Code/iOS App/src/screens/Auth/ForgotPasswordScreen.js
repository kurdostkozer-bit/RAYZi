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
import Icon from 'react-native-vector-icons/Ionicons';

import {Button, Input, Loading} from '../../components';
// تصحيح مسار استدعاء الخدمة المباشر لمنع أخطاء الـ undefined
import authService from '../../services/authService';

const ForgotPasswordScreen = ({navigation}) => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [emailSent, setEmailSent] = useState(false);

  const validateEmail = () => {
    if (!email) {
      setError('البريد الإلكتروني مطلوب');
      return false;
    }
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('البريد الإلكتروني غير صالح');
      return false;
    }
    setError('');
    return true;
  };

  const handleSubmit = async () => {
    if (!validateEmail()) return;

    setLoading(true);

    try {
      await authService.forgotPassword(email);
      setEmailSent(true);
    } catch (error) {
      setError(error?.message || 'فشل إرسال البريد الإلكتروني، تحقق من السيرفر');
    } finally {
      setLoading(false);
    }
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
            <TouchableOpacity
              style={styles.backButtonTop}
              onPress={() => navigation.goBack()}>
              <Icon name="arrow-back" size={24} color="#fff" />
            </TouchableOpacity>

            {!emailSent ? (
              <>
                <View style={styles.header}>
                  <View style={styles.iconContainer}>
                    <Icon name="mail-unread" size={60} color="#fff" />
                  </View>
                  <Text style={styles.title}>نسيت كلمة المرور?</Text>
                  <Text style={styles.subtitle}>
                    أدخل بريدك الإلكتروني وسنرسل لك رابطاً لإعادة تعيين كلمة المرور
                  </Text>
                </View>

                <View style={styles.form}>
                  <Input
                    label="البريد الإلكتروني"
                    placeholder="أدخل بريدك الإلكتروني"
                    value={email}
                    onChangeText={setEmail}
                    keyboardType="email-address"
                    autoCapitalize="none"
                    icon="mail-outline"
                    error={error}
                  />

                  <Button
                    title="إرسال رابط إعادة التعيين"
                    onPress={handleSubmit}
                    loading={loading}
                    style={styles.submitButton}
                  />
                </View>
              </>
            ) : (
              <View style={styles.successContainer}>
                <View style={styles.successIconContainer}>
                  <Icon name="checkmark-circle" size={80} color="#4CAF50" />
                </View>
                <Text style={styles.successTitle}>تم الإرسال بنجاح!</Text>
                <Text style={styles.successText}>
                  تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني
                </Text>
                <Text style={styles.emailText}>{email}</Text>

                <Button
                  title="العودة لتسجيل الدخول"
                  onPress={() => navigation.navigate('Login')}
                  style={styles.submitButtonSuccess}
                />
              </View>
            )}
          </ScrollView>
        </KeyboardAvoidingView>
      </LinearGradient>
      <Loading visible={loading} text="جاري الإرسال..." />
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
  // تصحيح التسمية لزر العودة العلوي لمنع التعارض الحرج
  backButtonTop: {
    position: 'absolute',
    top: 20,
    left: 20,
    zIndex: 1,
  },
  header: {
    alignItems: 'center',
    marginBottom: 30,
  },
  iconContainer: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: 'rgba(255, 255, 255, 0.2)',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 10,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 14,
    color: 'rgba(255, 255, 255, 0.9)',
    textAlign: 'center',
    paddingHorizontal: 20,
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
  submitButton: {
    marginTop: 10,
  },
  // تصحيح تسمية زر العودة السفلي ليعمل باستقرار برمي
  submitButtonSuccess: {
    marginTop: 10,
    width: '100%',
  },
  successContainer: {
    alignItems: 'center',
    padding: 30,
  },
  successIconContainer: {
    marginBottom: 20,
  },
  successTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 15,
  },
  successText: {
    fontSize: 16,
    color: 'rgba(255, 255, 255, 0.9)',
    textAlign: 'center',
    marginBottom: 10,
  },
  emailText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 30,
  },
});

export default ForgotPasswordScreen;
