// useLoading.js
import { ref } from 'vue';

export function useLoading(name, initialValue = false) {
  // 参数校验：name 必须是非空字符串
  if (typeof name !== 'string' || !name.trim()) {
    throw new Error('useLoading: first argument "name" must be a non-empty string');
  }

  const loading = ref(initialValue);
  const capName = name.charAt(0).toUpperCase() + name.slice(1); // 首字母大写

  const start = () => {
    loading.value = true;
  };

  const stop = () => {
    loading.value = false;
  };

  return {
    [`${name}Loading`]: loading,       // 动态命名：userLoading
    [`start${capName}Loading`]: start, // 动态命名：startUserLoading
    [`stop${capName}Loading`]: stop    // 动态命名：stopUserLoading
  };
}



/**
 使用示例：
 
 <template>
  <div>
    <!-- 直接使用动态生成的变量名 -->
    <div v-if="userLoading" class="loading-indicator">用户数据加载中...</div>
    
    <button 
      @click="handleFetch" 
      :disabled="userLoading"
    >
      {{ userLoading ? '加载中...' : '获取用户数据' }}
    </button>
  </div>
</template>

<script>
import { useLoading } from './useLoading';
import { fetchUserDataAPI } from '@/api';

export default {
  setup() {
    // 严格按您的要求调用：
    // 第一个参数 = name ('user')
    // 第二个参数 = initialValue (false)
    const { 
      userLoading,       // 响应式 loading 状态
      startUserLoading,  // 启动方法
      stopUserLoading    // 停止方法
    } = useLoading('user', false);

    // 手动控制 loading 状态
    const handleFetch = async () => {
      startUserLoading(); // 语义化启动
      
      try {
        await fetchUserDataAPI();
      } catch (error) {
        console.error('获取用户数据失败', error);
      } finally {
        stopUserLoading(); // 语义化停止
      }
    };

    return {
      userLoading,
      handleFetch
    };
  }
};
</script>
 */