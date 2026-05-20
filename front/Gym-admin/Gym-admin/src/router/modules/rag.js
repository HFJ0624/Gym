const Layout = () => import('@/layout/index.vue')
const ragKnowledge = () => import('@/views/rag/rag.vue')
const ragEval = () => import('@/views/rag/ragEval.vue')
const mcpDocImport = () => import('@/views/rag/mcpDocImport.vue')


export default [
    {
        path: '/rag',
        component: Layout,
        name: 'rag',
        meta: {
            title: 'RAG应用',
        },
        icon: 'Location',
        children: [
            {
                path: '/ragKnowledge',
                name: 'ragKnowledge',
                component: ragKnowledge,
                meta: {
                    title: 'RAG场馆知识库问答',
                },
                icon: 'Message',
                hidden: false
            },
            {
                path: '/ragEval',
                name: 'ragEval',
                component: ragEval,
                meta: {
                    title: 'RAG评估',
                },
                icon: 'Message',
                hidden: false
            },
            {
                path: '/mcpDoc',
                name: 'mcpDoc',
                component: mcpDocImport,
                meta: {
                    title: 'MCP文档导入RAG',
                },
                icon: 'Message',
                hidden: false
            },
        ],
    },
]