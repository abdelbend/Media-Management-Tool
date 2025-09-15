import { motion } from "framer-motion";
import {
  Book,
  Users,
  Smartphone,
  Shield,
  TrendingUp,
  Scan,
} from "lucide-react";
import { Link } from "react-router-dom";
const paragraph =
  " Track, organize, and manage your books, movies, games, and more effortlessly.";

export default function LandingPage() {
  return (
    <div className="bg-gradient-to-r from-yellow-400 via-orange-300 to-yellow-200 text-gray-800 w-full overflow-auto">
      <section className="relative flex flex-col items-center justify-center mt-10">
        <div className="text-center px-6 hover:animate-pulse hover:scale-105 transform transition duration-400 ease-in-out">
          <motion.h1
            className="text-2xl sm:text-2xl md:text-4xl font-bold mb-4"
            initial={{ opacity: 0, x: -50, y: 0 }}
            animate={{ opacity: 1, x: 0, y: 0 }}
            transition={{ duration: 1, delay: 0.5 }}
          >
            Manage Your Media Seamlessly
          </motion.h1>
          <motion.div
            className="text-lg md:text-xl text-gray-200"
            initial="hidden"
            animate="visible"
            variants={{
              hidden: { opacity: 0 },
              visible: {
                opacity: 1,
                transition: {
                  staggerChildren: 0.2, // Time between each word animation
                },
              },
            }}
            transition={{ duration: 1, delay: 0.5 }}
            style={{
              fontSize: "1rem",
              isolation: "isolate",
            }}
          >
            <motion.p className="text-center text-gray-800">
              {paragraph.split(" ").map((word, index) => (
                <motion.span
                  key={index}
                  variants={{
                    hidden: { opacity: 0, x: -10 },
                    visible: { opacity: 1, x: 0 },
                  }}
                  transition={{
                    duration: 0.5,
                    ease: "easeInOut",
                  }}
                  style={{ display: "inline-block", marginRight: "4px" }}
                >
                  {word}
                </motion.span>
              ))}
            </motion.p>
          </motion.div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 px-4">
        <h2 className="text-3xl md:text-4xl font-bold text-center mb-10 hover:animate-pulse hover:scale-105 transform transition duration-400 ease-in-out text-gray-800">
          Features
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          {[
            { icon: Book, text: "Add and Organize Media" },
            { icon: Users, text: "Track Loans Effortlessly" },
            { icon: Smartphone, text: "Mobile Barcode Scanning" },
            { icon: Shield, text: "Encrypted Data Storage" },
            { icon: TrendingUp, text: "Generate Detailed Statistics" },
            { icon: Scan, text: "Auto-fetch Metadata" },
          ].map((feature, index) => (
            <motion.div
              key={index}
              className="
              p-6 bg-white rounded-lg shadow-lg flex items-center 
              transform transition duration-400 ease-in-out 
              hover:-translate-y-1 hover:shadow-2xl 
             hover:bg-blue-100
            "
              whileHover={{
                y: -5,
                boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.5)",
              }}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
            >
              <feature.icon size={40} className="text-indigo-500 mr-4" />
              <p className="text-lg">{feature.text}</p>
            </motion.div>
          ))}
        </div>
      </section>

      {/* How It Works Section */}
      <section className="py-2">
        <h2 className="text-3xl md:text-4xl font-bold text-center mb-12 hover:animate-pulse hover:scale-105 transform transition duration-400 ease-in-out text-gray-800">
          How It Works
        </h2>
        <div className="flex flex-col md:flex-row justify-center items-center md:space-x-8 mx-4">
          {[
            "Insert Person details and then track their medias.",
            "Add your media easily by scanning or entering details.",
            "Organize your media and coresponding categories.",
            "Track loans with reminders and due dates.",
          ].map((step, index) => (
            <motion.div
              key={index}
              className="p-4 bg-white rounded-lg shadow-lg w-80 mb-6 md:mb-0 transform transition duration-400 ease-in-out 
              hover:-translate-y-3 hover:shadow-2xl hover:bg-blue-100"
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              whileHover={{
                y: -5,
                boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.5)",
              }}
            >
              <h3 className="text-xl font-semibold mb-4">{`Step ${
                index + 1
              }`}</h3>
              <p>{step}</p>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Call to Action Section */}
      <section className="py-16 text-white">
        <div className="text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-6 hover:animate-pulse hover:scale-105 transform transition duration-400 ease-in-out text-gray-800">
            Ready to Get Started?
          </h2>
          <div className="flex justify-center space-x-4">
            <Link to="/signup">
              <motion.button
                className="px-6 py-3 bg-indigo-600 rounded-lg shadow-md hover:bg-indigo-700 transition"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Register
              </motion.button>
            </Link>
            <Link to="/login">
              <motion.button
                className="px-6 py-3 bg-gray-100 text-gray-800 rounded-lg shadow-md hover:bg-gray-200 transition"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Login
              </motion.button>
            </Link>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gradient-to-r from-yellow-400 via-orange-300 to-yellow-200 text-gray-800 py-6  mt-12">
        <div className="max-w-7xl mx-auto px-6 flex flex-col items-center space-y-6 md:space-y-0  md:flex-row md:justify-center">
          <div className="flex items-center space-x-4">
            <p className="text-xl font-semibold hover:scale-105 transition-transform duration-300">
              This project was developed by the{" "}
              <span className="font-bold text-indigo-600">AdamPos</span> team.
            </p>
            <img
              src="../../AdamPos.png"
              alt="AdamPos Team Logo"
              className="h-16 w-16 rounded-full shadow-lg border-2 border-gray-900 
             hover:scale-150 hover:shadow-xl transition-transform duration-300 ease-in-out"
            />
          </div>
        </div>
      </footer>
    </div>
  );
}
