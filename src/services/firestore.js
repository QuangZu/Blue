import firestore from "@react-native-firebase/firestore";

export const getUserDocument = async (uid) => {
  try {
    const userDocument = await firestore().collection('users').doc(uid).get();
    return { 
      success: true, 
      exists: userDocument.exists,
      data: userDocument.exists ? userDocument.data() : null 
    };
  } catch (error) {
    console.error("Firestore error:", error);
    return { success: false, error };
  }
};

export const createUserDocument = async (uid, userData) => {
  try {
    await firestore().collection('users').doc(uid).set(userData);
    return { success: true };
  } catch (error) {
    console.error("Create user error:", error);
    return { success: false, error };
  }
};

// Add more Firestore operations as needed