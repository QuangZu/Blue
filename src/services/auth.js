import auth from "@react-native-firebase/auth";

export const signInWithPhoneNumber = async (phoneNumber) => {
  try {
    const confirmation = await auth().signInWithPhoneNumber(phoneNumber);
    return { success: true, confirmation };
  } catch (error) {
    console.error("Sign in error:", error);
    return { success: false, error };
  }
};

export const confirmCode = async (confirmation, code) => {
  try {
    const userCredential = await confirmation.confirm(code);
    return { success: true, user: userCredential.user };
  } catch (error) {
    console.error("Confirmation error:", error);
    return { success: false, error };
  }
};

export const getCurrentUser = () => {
  return auth().currentUser;
};

export const signOut = async () => {
  try {
    await auth().signOut();
    return { success: true };
  } catch (error) {
    console.error("Sign out error:", error);
    return { success: false, error };
  }
};